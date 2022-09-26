CREATE TABLE "public"."${device_10minute_history}" (
                                     "id" serial4,
                                     "station_id" int4 NOT NULL,
                                     "value" float4 NOT NULL,
                                     "time" timestamp NOT NULL,
                                     PRIMARY KEY ("id")
)
;

COMMENT ON COLUMN "public"."${device_10minute_history}"."station_id" IS '站点id';

COMMENT ON COLUMN "public"."${device_10minute_history}"."value" IS '值';

COMMENT ON COLUMN "public"."${device_10minute_history}"."time" IS '时间';