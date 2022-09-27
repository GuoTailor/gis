CREATE TABLE "public"."${device_day_history}" (
                                     "id" int8 NOT NULL,
                                     "station_id" int4 NOT NULL,
                                     "value" float4 NOT NULL,
                                     "time" date NOT NULL,
                                     PRIMARY KEY ("id")
)
;

CREATE INDEX ON "public"."${device_day_history}" USING btree (
                                                 "station_id"
    );

COMMENT ON COLUMN "public"."${device_day_history}"."station_id" IS '站点id';

COMMENT ON COLUMN "public"."${device_day_history}"."value" IS '值';

COMMENT ON COLUMN "public"."${device_day_history}"."time" IS '时间';
