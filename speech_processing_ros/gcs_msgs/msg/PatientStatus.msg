Header header

int32 patient_id
int32  age                  # rough estimate of persons age; defaults to -1 if not set
int32  gender               # 0 = unknown, 1 = male, 2 = female, default 0
int32  gesture              # GESTURE_WAVING = 0, GESTURE_SWIPE_LEFT = 1, GESTURE_SWIPE_RIGHT = 2, GESTURE_SWIPE_UP = 3,
                            # GESTURE_SWIPE_DOWN = 4, GESTURE_PUSH = 5, default -1

bool are_eyes_open          #default false
bool is_mouth_open          #default false

bool has_moved              # default false
int8 has_moved_head         # default -1
int8 has_moved_left_arm     # default -1
int8 has_moved_left_hand    # default -1
int8 has_moved_left_leg     # default -1
int8 has_moved_right_arm    # default -1
int8 has_moved_right_hand   # default -1
int8 has_moved_right_leg    # default -1

float32 joint_position_head_confidence
float32 joint_position_neck_confidence # -
float32 joint_position_spine_top_confidence # -
float32 joint_position_spine_mid_confidence # -
float32 joint_position_left_shoulder_confidence # -
float32 joint_position_left_elbow_confidence
float32 joint_position_left_hand_confidence
float32 joint_position_right_shoulder_confidence # -
float32 joint_position_right_elbow_confidence
float32 joint_position_right_hand_confidence
float32 joint_position_right_wrist_confidence # -
float32 joint_position_left_wrist_confidence # -
float32 joint_position_left_hip_confidence # -
float32 joint_position_left_knee_confidence
float32 joint_position_left_ankle_confidence
float32 joint_position_right_hip_confidence
float32 joint_position_right_knee_confidence
float32 joint_position_right_ankle_confidence
