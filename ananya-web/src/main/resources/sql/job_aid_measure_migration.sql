ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT fk_ja_content_measure_ja_content_dimension;
ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT fk_job_aid_content_measure_flw_dimension;
ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT fk_job_aid_content_measure_location_dimension;
ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT fk_job_aid_content_measure_time_dimension;
ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT fk_job_aid_content_measure_language_dimension;
ALTER TABLE report.job_aid_content_measure DROP CONSTRAINT pk_job_aid_content_measure;
DROP INDEX report.job_aid_content_measure_location_id_idx;

ALTER TABLE report.job_aid_content_measure RENAME TO job_aid_content_measure_temp;

CREATE TABLE report.job_aid_content_measure
(
  id                 INTEGER NOT NULL,
  flw_id             BIGINT,
  call_id            CHARACTER VARYING(255),
  location_id        BIGINT,
  job_aid_content_id INTEGER,
  "timestamp"        TIMESTAMP WITH TIME ZONE,
  percentage         SMALLINT,
  time_id            BIGINT,
  duration           INTEGER,
  language_id        INTEGER NOT NULL,
  flw_history_id     INTEGER
)
WITH (
OIDS = FALSE
);


INSERT INTO report.job_aid_content_measure
(
  id,
  flw_id,
  call_id,
  location_id,
  job_aid_content_id,
  "timestamp",
  percentage,
  time_id,
  duration,
  language_id,
  flw_history_id
)
  (
    SELECT
      d.id,
      d.flw_id,
      d.call_id,
      d.location_id,
      d.job_aid_content_id,
      d."timestamp",
      d.percentage,
      d.time_id,
      d.duration,
      d.language_id,
      h.id AS flw_history_id
    FROM
        report.job_aid_content_measure_temp d
        JOIN report.front_line_worker_history h
          ON (h.flw_id = d.flw_id)
  );



ALTER TABLE report.job_aid_content_measure ADD CONSTRAINT pk_job_aid_content_measure PRIMARY KEY (id);

ALTER TABLE report.job_aid_content_measure ADD CONSTRAINT fk_ja_content_measure_ja_content_dimension FOREIGN KEY (job_aid_content_id)
REFERENCES report.job_aid_content_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.job_aid_content_measure ADD CONSTRAINT fk_job_aid_content_measure_flw_dimension FOREIGN KEY (flw_id)
REFERENCES report.front_line_worker_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.job_aid_content_measure ADD CONSTRAINT fk_job_aid_content_measure_location_dimension FOREIGN KEY (location_id)
REFERENCES report.location_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.job_aid_content_measure ADD CONSTRAINT fk_job_aid_content_measure_time_dimension FOREIGN KEY (time_id)
REFERENCES report.time_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.job_aid_content_measure
ADD CONSTRAINT fk_job_aid_content_measure_language_dimension FOREIGN KEY (language_id)
REFERENCES report.language_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE INDEX job_aid_content_measure_location_id_idx
ON report.job_aid_content_measure
USING BTREE
(location_id);

ALTER SEQUENCE report.job_aid_content_measure_id_seq OWNED BY report.call_duration_measure.id;
ALTER TABLE report.job_aid_content_measure ALTER COLUMN id SET DEFAULT nextval('report.job_aid_content_measure_id_seq');

ALTER TABLE report.job_aid_content_measure
OWNER TO postgres;
GRANT ALL ON TABLE report.job_aid_content_measure TO postgres;
GRANT SELECT ON TABLE report.job_aid_content_measure TO motech;


--DROP TABLE report.job_aid_content_measure_temp;