from morse.builder import *

from math import cos, sin, pi

class Rat(GroundRobot):
    
    IR_ANGLE = pi/8

    def __init__(self, name = None, debug = True):

        # rat.blend is located in the data/robots directory
        GroundRobot.__init__(self, 'morris/robots/rat.blend', name)
        self.properties(classpath = "morris.robots.rat.Rat")

        ###################################
        # Actuators
        ###################################
        #wpy = Waypoint('wpy')
        #wpy.properties(Speed=2.0, Tolerance=0.1)
        #self.append(wpy)
        vw = MotionVW('vw')
        self.append(vw)
        
        #keyboard = Keyboard()
        #self.append(keyboard)
        #keyboard.properties(ControlType = 'Position')

        ###################################
        # Sensors
        ###################################
        pose = Pose()
        pose.translate(z = 1)
        self.append(pose)

        # Infrared sensors
        fir = Infrared(name='frontir')
        fir.translate(x=1)
        self.append(fir)
        
        lir = Infrared(name='leftir')
        lir.rotate(z=self.IR_ANGLE)
        lir.translate(x=cos(self.IR_ANGLE),y=sin(self.IR_ANGLE))
        self.append(lir)
        
        rir = Infrared(name='rightir')
        rir.rotate(z=-self.IR_ANGLE) 
        rir.translate(x=cos(self.IR_ANGLE),y=-sin(self.IR_ANGLE))
        self.append(rir)
        
        # Semantic camera
        semcam = SemanticCamera()
        semcam.translate(1, 0, 1)
        semcam.properties(relative=True)
        self.append(semcam)
        
        # Interfaces
        pose.add_interface('socket')
        vw.add_interface('socket')
        
        fir.add_stream('socket')
        lir.add_stream('socket')
        rir.add_stream('socket')

        semcam.add_interface('socket')

