CREATE TABLE "public"."${device_10minute_history}" (
                                     "id" int8 NOT NULL,
                                     "station_id" int4 NOT NULL,
                                     "value" float4 NOT NULL,
                                     "time" timestamp NOT NULL,
                                     PRIMARY KEY ("id")
)
;

CREATE INDEX ON "public"."${device_10minute_history}" USING btree (
                                                              "station_id"
    );

COMMENT ON COLUMN "public"."${device_10minute_history}"."station_id" IS '站点id';

COMMENT ON COLUMN "public"."${device_10minute_history}"."value" IS '值';

COMMENT ON COLUMN "public"."${device_10minute_history}"."time" IS '时间';