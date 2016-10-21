#! /usr/bin/env morseexec

""" Basic MORSE simulation scene for <morris> environment

Feel free to edit this template as you like!
"""

from morse.builder import *
from morris.builder.robots import Rat

from math import cos, sin, pi



robot = Rat()
robot.translate(0,0,.075)

# Semantic camera
# book = PassiveObject('props/misc_objects','Book_Blue_medium')
# book.properties(Object=True) 
# book.translate(0.6, 0.6, 10)


env = Environment('environments/circularMorris.blend')
env.use_internal_syncer()
env.set_time_scale(.2)
#env.simulator_frequency(20)  
robot.frequency(10)
env.show_framerate()

