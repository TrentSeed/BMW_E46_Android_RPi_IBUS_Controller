"""
The pavement.py module defines various Paver tasks. Tasks include unit_tests, pylint, etc.

Usage
-----
    # execute the entire build pipeline
    $ paver
    ---> pavement.default
    ---> pavement.unit_tests
    nosetests --with-xcoverage --with-xunit --cover-package=controllers,interfaces --cover-erase
    ...

    # execute a particular task of the pipeline (method name)
    $ paver pylint
    ---> pavement.pylint
    pylint -f parseable controllers interfaces | tee pylint.out
    ...

"""
from paver.tasks import BuildFailure, needs, task
from paver.easy import sh


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
def pylint():
    """Invoke pylint on remote_code_block and libs packages."""
    try:
        sh('pylint -f parseable --rcfile .pylintrc \
            controllers interfaces start_controller.py | tee pylint.out')
    except BuildFailure:
        pass  # pylint errors should NOT fail the build


@needs('unit_tests', 'pylint')
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
