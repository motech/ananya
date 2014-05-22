ALTER TABLE report.call_duration_measure DROP CONSTRAINT fk_call_duration_measure_flw_dimension;
ALTER TABLE report.call_duration_measure DROP CONSTRAINT fk_call_duration_measure_location_dimension;
ALTER TABLE report.call_duration_measure DROP CONSTRAINT fk_call_duration_measure_time_dimension;
ALTER TABLE report.call_duration_measure DROP CONSTRAINT pk_call_duration_measure;
DROP INDEX report.call_duration_measure_location_id_idx;
DROP INDEX report.idx_call_duration_measure_flw_id;
ALTER TABLE report.call_duration_measure RENAME TO call_duration_measure_temp;

CREATE TABLE report.call_duration_measure
(
  id                INTEGER               NOT NULL,
  flw_id            BIGINT,
  call_id           CHARACTER VARYING(255),
  duration          INTEGER,
  type              CHARACTER VARYING(30),
  location_id       BIGINT,
  start_time        TIMESTAMP WITH TIME ZONE,
  end_time          TIMESTAMP WITH TIME ZONE,
  called_number     BIGINT,
  time_id           BIGINT,
  duration_in_pulse INTEGER,
  operator          CHARACTER VARYING(30) NOT NULL,
  flw_history_id    INTEGER
)
WITH (
OIDS = FALSE
);


INSERT INTO report.call_duration_measure
(
  id,
  flw_id,
  call_id,
  duration,
  type,
  location_id,
  start_time,
  end_time,
  called_number,
  time_id,
  duration_in_pulse,
  operator,
  flw_history_id
)
  (
    SELECT
      d.id,
      d.flw_id,
      d.call_id,
      d.duration,
      d.type,
      d.location_id,
      d.start_time,
      d.end_time,
      d.called_number,
      d.time_id,
      d.duration_in_pulse,
      d.operator,
      h.id AS flw_history_id
    FROM
        report.call_duration_measure_temp d
        JOIN report.front_line_worker_history h
          ON (h.flw_id = d.flw_id)
  );

ALTER TABLE report.call_duration_measure ADD CONSTRAINT pk_call_duration_measure PRIMARY KEY (id);

ALTER TABLE report.call_duration_measure
ADD CONSTRAINT fk_call_duration_measure_flw_dimension FOREIGN KEY (flw_id)
REFERENCES report.front_line_worker_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.call_duration_measure
ADD CONSTRAINT fk_call_duration_measure_location_dimension FOREIGN KEY (location_id)
REFERENCES report.location_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.call_duration_measure
ADD CONSTRAINT fk_call_duration_measure_time_dimension FOREIGN KEY (time_id)
REFERENCES report.time_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE INDEX call_duration_measure_location_id_idx
ON report.call_duration_measure
USING BTREE
(location_id);

CREATE INDEX idx_call_duration_measure_flw_id
  ON report.call_duration_measure
  USING btree
  (flw_id);

ALTER SEQUENCE report.call_duration_measure_id_seq OWNED BY report.call_duration_measure.id;
ALTER TABLE report.call_duration_measure ALTER COLUMN id SET DEFAULT nextval('report.call_duration_measure_id_seq');

ALTER TABLE report.call_duration_measure
OWNER TO postgres;
GRANT ALL ON TABLE report.call_duration_measure TO postgres;
GRANT SELECT ON TABLE report.call_duration_measure TO motech;

-- drop table report.call_duration_measure_temp;