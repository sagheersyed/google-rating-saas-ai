# Google Reviews Webhook & Reply Integration Verification Guide

This guide explains how to verify that a Google review webhook is processed end-to-end:

1. webhook is received,
2. review is validated and saved,
3. AI reply is generated,
4. reply is posted back to Google.

## 1) Prerequisites

- A business row exists in DB with:
  - `google_place_id` matching the webhook payload place id,
  - active subscription flags (`is_subscribed=true`, `is_active=true`),
  - non-expired `subscription_expiry_date`.
- `GOOGLE_PLACES_API_KEY` is configured.
- App is running and `/api/reviews/webhook/google-reviews` is reachable.

## 2) Unit-Level Verification (fast)

Run focused tests for webhook and service wiring:

```bash
mvn -Dtest=GoogleReviewsWebhookHandlerTest,ReviewServiceGoogleFlowTest test
```

Expected result:
- handler test verifies valid payload calls `ReviewService.processGoogleReview(...)`.
- service test verifies Google reply publish call executes using generated AI reply.

## 3) Manual API Verification (local)

Send a payload that matches your business `google_place_id`:

```bash
curl -X POST http://localhost:8080/api/reviews/webhook/google-reviews \
  -H "Content-Type: application/json" \
  -H "X-Hub-Signature: test-signature" \
  -d '{
    "review": {
      "name": "places/chij111/reviews/rev_777",
      "rating": 5,
      "comment": "Great service and clean environment"
    }
  }'
```

### What to check after call

- In app logs, you should see success message from webhook handler.
- In DB `reviews` table, record should exist with:
  - `google_review_id = places/chij111/reviews/rev_777`,
  - `replied = true`,
  - `ai_reply` populated.
- In Google Business Profile, reply should appear under that review.

## 4) Failure-path checks

- Send duplicate review id => should be skipped safely.
- Send rating outside 1..5 => should be rejected.
- Send empty comment => should be rejected.
- Set business subscription inactive => should not generate/publish reply.

## 5) Known limitation

- Signature verification currently returns `true` by placeholder implementation; before production use, replace with real HMAC/signature verification.
