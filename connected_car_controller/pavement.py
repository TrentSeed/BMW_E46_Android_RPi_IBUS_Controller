"""
The pavement.py module defines the various Paver tasks. Tasks include unit_tests,
lettuce_tests, pylint, etc.

Usage
-----
    # execute the entire build pipeline
    $ paver
    ---> pavement.default
    ---> pavement.unit_tests
    nosetests --with-xcoverage --with-xunit --cover-package=controllers,interfaces --cover-erase
    ...

    # execute a particular task of the pipeline (method name)
    $ paver run_pylint
    ---> pavement.run_pylint
    pylint -f parseable controllers interfaces | tee pylint.out
    ...

"""
from paver.tasks import BuildFailure, needs, task
from paver.setuputils import setup, find_packages
from paver.easy import sh


setup(
    name='ConnectedCarController',
    version='1.0.0',
    packages=find_packages(),
    license=open('LICENSE.txt').read(),
    long_description=open('README.txt').read(),
    author='Trent Seed',
    author_email='tmseed@gmail.com'
)


@needs(['distutils.command.sdist'])
@task
def sdist():
    """Creates a deployment artifact in dist/*."""
    pass


@task
def unit_tests():
    """Invoke nosetests runner for all test/unit/* unit tests."""
    settings = [
        '--with-xcoverage',
        '--with-xunit',
        '--cover-package=controllers,interfaces',
        '--cover-erase'
    ]
    sh('nosetests test/unit {}'.format(' '.join(settings)))


@task
def lettuce_tests():
    """Invoke lettuce running for all test/bdd/* feature tests."""
    sh('lettuce test/bdd')


@task
def run_pylint():
    """Invoke pylint on remote_code_block and libs packages."""
    try:
        sh('pylint -f parseable --rcfile .pylintrc \
            controllers interfaces start_controller.py | tee pylint.out')
    except BuildFailure:
        pass  # pylint errors should NOT fail the build


@needs('unit_tests', 'lettuce_tests', 'run_pylint', 'sdist')
@task
def default():
    """The default task executed if `paver` is invoked with 0 arguments.

    A critical error in any of the stages will result in a FAILED build.

    Build Pipeline
    --------------
        - unit tests are exercised
        - behavioral feature tests are exercised
        - pylint scores the entire codebase
        - a deployment artifact is created

    """
    pass
