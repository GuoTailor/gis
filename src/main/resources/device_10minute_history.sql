CREATE TABLE "public"."${device_10minute_history}"
(
    "id"             int8          NOT NULL,
    "station_id"     int4          NOT NULL,
    "value"          decimal(5, 3) NOT NULL,
    "time"           timestamp     NOT NULL,
    "alarm_state"    varchar(16)   NOT NULL,
    "cancel_time"    timestamp,
    "screenshot_url" varchar(255) COLLATE "pg_catalog"."default",
    "cancel_alarm"   bool,
    PRIMARY KEY ("id")
)
;

CREATE INDEX ON "public"."${device_10minute_history}" USING btree (
                                                                   "station_id"
    );

COMMENT ON COLUMN "public"."${device_10minute_history}"."station_id" IS '站点id';
COMMENT ON COLUMN "public"."${device_10minute_history}"."value" IS '值';
COMMENT ON COLUMN "public"."${device_10minute_history}"."time" IS '时间';
COMMENT ON COLUMN "public"."${device_10minute_history}"."alarm_state" IS '报警状态';
COMMENT ON COLUMN "public"."${device_10minute_history}"."cancel_time" IS '取消报警时间';
COMMENT ON COLUMN "public"."${device_10minute_history}"."screenshot_url" IS '报警相机截图';
COMMENT ON COLUMN "public"."${device_10minute_history}"."cancel_alarm" IS '是否取消报警';