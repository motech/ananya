ALTER TABLE report.sms_sent_measure DROP CONSTRAINT fk_sms_sent_measure_flw_dimension;
ALTER TABLE report.sms_sent_measure DROP CONSTRAINT fk_sms_sent_measure_location_dimension;
ALTER TABLE report.sms_sent_measure DROP CONSTRAINT fk_sms_sent_measure_time_dimension;
ALTER TABLE report.sms_sent_measure DROP CONSTRAINT pk_sms_sent_measure;

DROP INDEX report.sms_sent_measure_location_id_idx;
DROP INDEX report.idx_sms_sent_measure_flw_id;
DROP INDEX report.idx_sms_sent_measure_time_id;

ALTER TABLE report.sms_sent_measure RENAME TO sms_sent_measure_temp;

CREATE TABLE report.sms_sent_measure
(
  id                   INTEGER NOT NULL,
  flw_id               BIGINT,
  time_id              INTEGER,
  course_attempt       INTEGER,
  sms_sent             BOOLEAN,
  sms_reference_number CHARACTER VARYING(30),
  location_id          BIGINT,
  flw_history_id       INTEGER
)
WITH (
OIDS = FALSE
);


INSERT INTO report.sms_sent_measure
(
  id,
  flw_id,
  time_id,
  course_attempt,
  sms_sent,
  sms_reference_number,
  location_id,
  flw_history_id
)
  (
    SELECT
      d.id,
      d.flw_id,
      d.time_id,
      d.course_attempt,
      d.sms_sent,
      d.sms_reference_number,
      d.location_id,
      h.id AS flw_history_id
    FROM
        report.sms_sent_measure_temp d
        JOIN report.front_line_worker_history h
          ON (h.flw_id = d.flw_id)
  );

ALTER TABLE report.sms_sent_measure ADD CONSTRAINT pk_sms_sent_measure PRIMARY KEY (id);

ALTER TABLE report.sms_sent_measure
ADD CONSTRAINT fk_sms_sent_measure_flw_dimension FOREIGN KEY (flw_id)
REFERENCES report.front_line_worker_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.sms_sent_measure
ADD CONSTRAINT fk_sms_sent_measure_location_dimension FOREIGN KEY (location_id)
REFERENCES report.location_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.sms_sent_measure
ADD CONSTRAINT fk_sms_sent_measure_time_dimension FOREIGN KEY (time_id)
REFERENCES report.time_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE INDEX idx_sms_sent_measure_time_id
ON report.sms_sent_measure
USING BTREE
(time_id);


CREATE INDEX idx_sms_sent_measure_flw_id
ON report.sms_sent_measure
USING BTREE
(flw_id);

CREATE INDEX sms_sent_measure_location_id_idx
ON report.sms_sent_measure
USING BTREE
(location_id);


ALTER SEQUENCE report.sms_sent_measure_id_seq OWNED BY report.call_duration_measure.id;
ALTER TABLE report.sms_sent_measure ALTER COLUMN id SET DEFAULT nextval('report.sms_sent_measure_id_seq');

ALTER TABLE report.sms_sent_measure
OWNER TO postgres;
GRANT ALL ON TABLE report.sms_sent_measure TO postgres;
GRANT SELECT ON TABLE report.sms_sent_measure TO motech;


--DROP TABLE report.sms_sent_measure_temp;