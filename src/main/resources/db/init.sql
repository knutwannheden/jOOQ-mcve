CREATE TABLE IF NOT EXISTS answer_def (
    answer_def_id   serial PRIMARY KEY,
    content         varchar(256) NOT NULL
);
CREATE TABLE IF NOT EXISTS question_def (
    question_def_id serial PRIMARY KEY,
    content         varchar(256) NOT NULL,
    answer_def_ids  integer[]
);
