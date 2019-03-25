import logging


from controllers.bmw.e46 import E46Controller


LOGGER = logging.getLogger(__name__)


class ControllerFactory(object):
    """Factory class to instantiate controllers provided the type."""

    @staticmethod
    def create(controller_type):
        if controller_type == 'bmw-e46':
            return E46Controller()

        LOGGER.error('controller not supported - %r', controller_type)
