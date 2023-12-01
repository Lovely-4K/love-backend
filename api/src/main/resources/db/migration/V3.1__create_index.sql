-- diary 인덱스 생성
CREATE INDEX idx_location_couple on diary(location_id, couple_id);
CREATE INDEX idx_couple_id_dating_day on diary(dating_day,couple_id);
CREATE INDEX idx_couple_id_score on diary(score,couple_id);

-- location 인덱스 생성
CREATE INDEX idx_location_latitude_longitude ON location(latitude, longitude);

-- question 인덱스 생성
CREATE INDEX idx_couple_boy_girl_choice ON question (couple_id, boy_choice_index, girl_choice_index);

-- calendar 인덱스 생성
CREATE INDEX idx_calendar_covering on calendar (id, start_date, end_date, schedule_details, schedule_type, owner_id, couple_id);
