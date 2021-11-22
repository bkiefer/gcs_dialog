Header header

int32 patient_id
int32  age     # rough estimate of persons age; defaults to -1 if not set
int32  gender  # 0 = unknown, 1 = male, 2 = female, default 0
int32  gesture  # GESTURE_WAVING = 0, GESTURE_SWIPE_LEFT = 1, GESTURE_SWIPE_RIGHT = 2,GESTURE_SWIPE_UP = 3, GESTURE_SWIPE_DOWN = 4, GESTURE_PUSH = 5, default -1

boolean are_eyes_open #default false
boolean is_mouth_open #default false

boolean has_moved # default false
int8 has_moved_head # default -1
int8 has_moved_left_arm # default -1
int8 has_moved_left_hand # default -1
int8 has_moved_left_leg # default -1
int8 has_moved_right_arm # default -1
int8 has_moved_right_hand # default -1
int8 has_moved_right_leg # default -1


