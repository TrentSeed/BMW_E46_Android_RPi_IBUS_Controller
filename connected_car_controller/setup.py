"""
The setup.py module is responsible for describing the metadata necessary
for building and installing the Python project.
"""
from distutils.core import setup
from version import __version__


setup(
    name='ConnectedCarController',
    version=__version__,
    packages=['controllers', 'interfaces', ],
    license=open('LICENSE.txt').read(),
    author='Trent Seed',
    author_email='hi@trentseed.com'
)
