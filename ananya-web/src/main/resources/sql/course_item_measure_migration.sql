ALTER TABLE report.course_item_measure DROP CONSTRAINT fk_course_item_measure_flw_dimension;
ALTER TABLE report.course_item_measure DROP CONSTRAINT fk_course_item_measure_location_dimension;
ALTER TABLE report.course_item_measure DROP CONSTRAINT fk_course_item_measure_time_dimension;
ALTER TABLE report.course_item_measure DROP CONSTRAINT fk_course_item_measure_course_item_dimension;
ALTER TABLE report.course_item_measure DROP CONSTRAINT fk_course_item_measure_language_dimension;
ALTER TABLE report.course_item_measure DROP CONSTRAINT pk_course_item_measure;

DROP INDEX report.course_item_measure_location_id_idx;
DROP INDEX report.idx_course_item_measure_course_item_id;
DROP INDEX report.idx_course_item_measure_flw_id;
DROP INDEX report.idx_course_item_measure_time_id;

ALTER TABLE report.course_item_measure RENAME TO course_item_measure_temp;

CREATE TABLE report.course_item_measure
(
  id             INTEGER NOT NULL,
  flw_id         BIGINT,
  course_item_id INTEGER,
  time_id        INTEGER,
  score          INTEGER,
  event          CHARACTER VARYING(30),
  location_id    BIGINT,
  "timestamp"    TIMESTAMP WITH TIME ZONE,
  percentage     SMALLINT,
  call_id        CHARACTER VARYING(255),
  duration       INTEGER,
  language_id    INTEGER NOT NULL,
  flw_history_id INTEGER
)
WITH (
OIDS = FALSE
);


INSERT INTO report.course_item_measure
(
  id,
  flw_id,
  course_item_id,
  time_id,
  score,
  event,
  location_id,
  "timestamp",
  percentage,
  call_id,
  duration,
  language_id,
  flw_history_id
)
  (
    SELECT
      d.id,
      d.flw_id,
      d.course_item_id,
      d.time_id,
      d.score,
      d.event,
      d.location_id,
      d."timestamp",
      d.percentage,
      d.call_id,
      d.duration,
      d.language_id,
      h.id AS flw_history_id
    FROM
        report.course_item_measure_temp d
        JOIN report.front_line_worker_history h
          ON (h.flw_id = d.flw_id)
  );

ALTER TABLE report.course_item_measure ADD CONSTRAINT pk_course_item_measure PRIMARY KEY (id);

ALTER TABLE report.course_item_measure
ADD CONSTRAINT fk_course_item_measure_course_item_dimension FOREIGN KEY (course_item_id)
REFERENCES report.course_item_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.course_item_measure
ADD CONSTRAINT fk_course_item_measure_flw_dimension FOREIGN KEY (flw_id)
REFERENCES report.front_line_worker_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.course_item_measure
ADD CONSTRAINT fk_course_item_measure_language_dimension FOREIGN KEY (language_id)
REFERENCES report.language_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.course_item_measure
ADD CONSTRAINT fk_course_item_measure_location_dimension FOREIGN KEY (location_id)
REFERENCES report.location_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE report.course_item_measure
ADD CONSTRAINT fk_course_item_measure_time_dimension FOREIGN KEY (time_id)
REFERENCES report.time_dimension (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE INDEX course_item_measure_location_id_idx
ON report.course_item_measure
USING BTREE
(location_id);

CREATE INDEX idx_course_item_measure_course_item_id
ON report.course_item_measure
USING BTREE
(course_item_id);

CREATE INDEX idx_course_item_measure_flw_id
ON report.course_item_measure
USING BTREE
(flw_id);

CREATE INDEX idx_course_item_measure_time_id
ON report.course_item_measure
USING BTREE
(time_id);

ALTER SEQUENCE report.course_item_measure_id_seq OWNED BY report.call_duration_measure.id;
ALTER TABLE report.course_item_measure ALTER COLUMN id SET DEFAULT nextval('report.course_item_measure_id_seq');

ALTER TABLE report.course_item_measure
OWNER TO postgres;
GRANT ALL ON TABLE report.course_item_measure TO postgres;
GRANT SELECT ON TABLE report.course_item_measure TO motech;


--DROP TABLE report.course_item_measure_temp;