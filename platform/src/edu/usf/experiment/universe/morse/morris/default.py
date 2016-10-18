#! /usr/bin/env morseexec

""" Basic MORSE simulation scene for <morris> environment

Feel free to edit this template as you like!
"""

from morse.builder import *
from morris.builder.robots import Rat

from math import cos, sin, pi

IR_ANGLE = pi/8

robot = Rat()

pose = Pose()
pose.translate(z = 1)
robot.append(pose)

motion = MotionVW()
robot.append(motion)

# Infrared sensors
fir = Infrared(name='frontir')
fir.translate(x=1)
robot.append(fir)
fir.add_stream('socket')

lir = Infrared(name='leftir')
lir.rotate(z=IR_ANGLE)
lir.translate(x=cos(IR_ANGLE),y=sin(IR_ANGLE))
robot.append(lir)
lir.add_stream('socket')

rir = Infrared(name='rightir')
rir.rotate(z=-IR_ANGLE) 
rir.translate(x=cos(IR_ANGLE),y=-sin(IR_ANGLE))
robot.append(rir)
rir.add_stream('socket')

# Semantic camera
cube = PassiveObject('props/misc_objects','Book_Blue_medium')
cube.properties(Object=True) 
cube.translate(0.6, 0.6, 10)

semcam = SemanticCamera()
semcam.translate(1, 0, 1)
semcam.properties(relative=True)
semcam.add_interface('socket')
robot.append(semcam)

keyboard = Keyboard()
robot.append(keyboard)
keyboard.properties(ControlType = 'Position')

# Add default interface for our robot's components
#atrv.add_default_interface('ros')

pose.add_interface('socket')
motion.add_interface('socket')

env = Environment('environments/emptySquare.blend', fastmode = True)
#env.set_time_scale(.05)
#env.simulator_frequency(30)  
#robot.frequency(1)
#env.use_internal_syncer()
