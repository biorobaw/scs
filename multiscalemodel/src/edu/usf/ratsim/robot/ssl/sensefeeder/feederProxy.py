from protobuf import connector_pb2 as proto

from ar_track_alvar_msgs.msg import AlvarMarker

import rospy
import tf 

max_wait = 2
cam_frame = "usb_cam"
marker_frame = "ar_marker_0"

if __name__ == "__main__":
    
    rospy.init_node('FeederProxy')
    
#     s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # Create a socket object
#     s.settimeout(None)
#     s.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)  # Send every packet individually
#     port = 12345  # Reserve a port for your service.
#     s.bind(("0.0.0.0", port))  # Bind to the por
#     rospy.loginfo("Socket initialized")
    
    tf_listener = tf.TransformListener()

    r = rospy.Rate(3) # 10hz
    while not rospy.is_shutdown():
        now = rospy.Time.now()
        markers = []
        lastT = getLatestCommonTime(cam_frame, marker_frame)
        if now - lastT < rospy.Duration(.3):
            (t, rot) = self.tf_listener.lookupTransform(cam_frame, marker_frame, lastT)
            print "Marca (1, x, y)", (id, t[0],  t[1]) 
        r.sleep()    
           