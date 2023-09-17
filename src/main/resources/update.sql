ALTER TABLE "public"."station"
    ADD COLUMN "code" varchar(16),
    ADD COLUMN "camera_ip" varchar(16),
    ADD COLUMN "play_url" varchar(128),
    ADD COLUMN "camera_type" varchar(4);

COMMENT ON COLUMN "public"."station"."code" IS '水电站编码';

COMMENT ON COLUMN "public"."station"."camera_ip" IS '相机ip地址';

COMMENT ON COLUMN "public"."station"."play_url" IS '相机播放地址';

COMMENT ON COLUMN "public"."station"."camera_type" IS '相机类型';

CREATE SEQUENCE "public"."examine_info_id_seq"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 2147483647
    START 1
    CACHE 1;

SELECT setval('"public"."examine_info_id_seq"', 956, true);

CREATE SEQUENCE "public"."target_rate_id_seq"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 2147483647
    START 1
    CACHE 1;

SELECT setval('"public"."target_rate_id_seq"', 661, true);


CREATE TABLE "public"."examine_info" (
                                         "id" int4 NOT NULL DEFAULT nextval('examine_info_id_seq'::regclass),
                                         "hyst_code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
                                         "eco_online" bool,
                                         "eco_flow" bool,
                                         "ass_per" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
                                         "ass_start" timestamp(6),
                                         "ass_end" timestamp(6),
                                         "rec_time" timestamp(6),
                                         "station_id" int4 NOT NULL,
                                         "station_count" int4,
                                         "flow_target_rate" numeric(5,4),
                                         "online_target_rate" numeric(5,4),
                                         CONSTRAINT "examine_info_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."examine_info"
    OWNER TO "postgres";

CREATE INDEX "examine_info_ass_start_ass_per_station_id_idx" ON "public"."examine_info" USING btree (
                                                                                                     "ass_start" "pg_catalog"."timestamp_ops" ASC NULLS LAST,
                                                                                                     "ass_per" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                                     "station_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

CREATE INDEX "examine_info_ass_start_idx" ON "public"."examine_info" USING btree (
                                                                                  "ass_start" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."examine_info"."id" IS 'id';

COMMENT ON COLUMN "public"."examine_info"."hyst_code" IS '水电站统计代码';

COMMENT ON COLUMN "public"."examine_info"."eco_online" IS '在线率是否合格';

COMMENT ON COLUMN "public"."examine_info"."eco_flow" IS '下泄率是否合格';

COMMENT ON COLUMN "public"."examine_info"."ass_per" IS '考核周期';

COMMENT ON COLUMN "public"."examine_info"."ass_start" IS '考核开始时间';

COMMENT ON COLUMN "public"."examine_info"."ass_end" IS '考核结束时间';

COMMENT ON COLUMN "public"."examine_info"."rec_time" IS '记录时间';

COMMENT ON COLUMN "public"."examine_info"."station_id" IS '水电站id';

COMMENT ON COLUMN "public"."examine_info"."station_count" IS '统计次数';

COMMENT ON COLUMN "public"."examine_info"."flow_target_rate" IS '下泄达标率';

COMMENT ON COLUMN "public"."examine_info"."online_target_rate" IS '在线达标率';


ALTER SEQUENCE "public"."examine_info_id_seq"
    OWNED BY "public"."examine_info"."id";

ALTER SEQUENCE "public"."examine_info_id_seq" OWNER TO "postgres";



CREATE TABLE "public"."target_rate" (
                                        "id" int4 NOT NULL DEFAULT nextval('target_rate_id_seq'::regclass),
                                        "datatime" timestamp(0) NOT NULL,
                                        "station_id" int4 NOT NULL,
                                        "target_rate" float4,
                                        "online_count" int2,
                                        "total_count" int2,
                                        CONSTRAINT "target_rate_pkey" PRIMARY KEY ("id"),
                                        CONSTRAINT "target_rate_station_id_fkey" FOREIGN KEY ("station_id") REFERENCES "public"."station" ("id") ON DELETE CASCADE ON UPDATE CASCADE
)
;

ALTER TABLE "public"."target_rate"
    OWNER TO "postgres";

CREATE INDEX "target_rate_datatime_idx" ON "public"."target_rate" USING btree (
                                                                               "datatime" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."target_rate"."datatime" IS '时间';

COMMENT ON COLUMN "public"."target_rate"."station_id" IS '站点id';

COMMENT ON COLUMN "public"."target_rate"."target_rate" IS '达标率';

COMMENT ON COLUMN "public"."target_rate"."online_count" IS '在线次数';

COMMENT ON COLUMN "public"."target_rate"."total_count" IS '总次数';

COMMENT ON TABLE "public"."target_rate" IS '水电站达标率';

ALTER SEQUENCE "public"."target_rate_id_seq"
    OWNED BY "public"."target_rate"."id";

ALTER SEQUENCE "public"."target_rate_id_seq" OWNER TO "postgres";