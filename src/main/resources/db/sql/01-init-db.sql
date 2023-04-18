CREATE TABLE "account" (
  "id" uuid PRIMARY KEY,
  "acc_no" int UNIQUE,
  "user_id" uuid,
  "currency_id" uuid,
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "user" (
  "id" uuid PRIMARY KEY,
  "name" varchar,
  "phone" varchar,
  "address" varchar,
  "created_at" timestamp
);

CREATE TABLE "transaction" (
  "id" uuid PRIMARY KEY,
  "sender_acc_id" uuid,
  "receiver_acc_id" uuid,
  "currency_id" uuid,
  "amount" int,
  "created_at" timestamp
);

CREATE TABLE "currency" (
  "id" uuid PRIMARY KEY,
  "code" varchar UNIQUE,
  "name" varchar
);

ALTER TABLE "account" ADD FOREIGN KEY ("currency_id") REFERENCES "currency" ("id");

ALTER TABLE "transaction" ADD FOREIGN KEY ("receiver_acc_id") REFERENCES "account" ("id");

ALTER TABLE "transaction" ADD FOREIGN KEY ("sender_acc_id") REFERENCES "account" ("id");

ALTER TABLE "account" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "transaction" ADD FOREIGN KEY ("currency_id") REFERENCES "currency" ("id");
