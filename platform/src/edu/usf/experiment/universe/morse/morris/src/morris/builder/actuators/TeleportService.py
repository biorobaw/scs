from morse.builder.creator import ActuatorCreator

class Teleportservice(ActuatorCreator):
    _classpath = "morris.actuators.TeleportService.Teleportservice"
    _blendname = "TeleportService"

    def __init__(self, name=None):
        ActuatorCreator.__init__(self, name)

