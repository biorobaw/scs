#! /usr/bin/env morseexec

""" Basic MORSE simulation scene for <morris> environment

Feel free to edit this template as you like!
"""

from morse.builder import *
from morris.builder.robots import Rat

# Add the MORSE mascott, MORSY.
# Out-the-box available robots are listed here:
# http://www.openrobots.org/morse/doc/stable/components_library.html
#
# 'morse add robot <name> morris' can help you to build custom robots.
robot = Rat()

pose = Pose()
pose.translate(z = 0.75)
robot.append(pose)

motion = MotionVW()
robot.append(motion)

keyboard = Keyboard()
robot.append(keyboard)
keyboard.properties(ControlType = 'Position')

# Add default interface for our robot's components
#atrv.add_default_interface('ros')

pose.add_stream('socket')
pose.add_service('socket')
motion.add_service('socket')

env = Environment('environments/emptySquare.blend')
