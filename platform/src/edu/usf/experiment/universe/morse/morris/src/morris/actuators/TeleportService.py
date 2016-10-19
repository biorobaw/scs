import logging; logger = logging.getLogger("morse." + __name__)

import morse.core.actuator

from morse.core.services import service, async_service, interruptible
from morse.core import status
from morse.helpers.components import add_data, add_property
from morse.core import mathutils
from morse.helpers.transformation import Transformation3d

class Teleportservice(morse.core.actuator.Actuator):
    """ The included Teleport service constantly teleports the robot, not allowing it to navigate freely. This teleport only teleports it upon request.
    """
    _name = "Teleportservice"
    _short_desc = "Teleports the robot only upon request, allowing other controllers to work properly in the robot, while providing seldomly executed teleport requests"

    def __init__(self, obj, parent=None):
        logger.info("%s initialization" % obj.name)
        # Call the constructor of the parent class
        morse.core.actuator.Actuator.__init__(self, obj, parent)

        self.actuator2robot = self.position_3d.transformation3d_with(self.robot_parent.position_3d)

        logger.info('Component initialized')

    @service
    def teleport(self, x = 0.0, y = 0.0, z = 0.0, roll = 0.0, pitch = 0.0, yaw=0.0):
        """
        Translate the actuator owner to the position given by the (x,y,z) and (roll,pitch,yaw) vectors.
        
        This is an **absolute** transformation.
        :param x: (default: 0.0) X translation, in meter
        :param y: (default: 0.0) Y translation, in meter
        :param z: (default: 0.0) Z translation, in meter
        :param roll: (default: 0.0) roll rotation, in radians
        :param pitch: (default: 0.0) pitch rotation, in radians
        :param yaw: (default: 0.0) yaw rotation, in radians
        """
        # New parent position
        position = mathutils.Vector((x,y,z))

        # New parent orientation
        orientation = mathutils.Euler((roll,pitch,yaw))

        world2actuator = Transformation3d(None)
        world2actuator.translation = position
        world2actuator.rotation = orientation

        (loc, rot, _) = (world2actuator.matrix *            self.actuator2robot.matrix).decompose()

        self.robot_parent.force_pose(loc, rot)

    def default_action(self):
        """ Main loop of the actuator.

        Implements the component behaviour
        """
        # This actuator does nothing periodically
        pass
