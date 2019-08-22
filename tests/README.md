# How to use

To use this test, the build environment needs to be setup first following [Setup build environment](../README.md#setup-build-environment),
or you can use docker for automation setup.
```sh
$ make -C ../docker build_test # to run build test
$ make -C ../docker ptest      # to run ptest
```

## build_test.sh
Script for building recipe in meta-debian.

Input params from env:

| Variable             | Description                     | Example                   |
| -------------------- | ------------------------------- | ------------------------- |
| TEST_TARGETS         | Recipes/Packages will be built. | "zlib core-image-minimal" |
| TEST_DISTROS         | Distros will be tested.         | "deby deby-tiny"          |
| TEST_MACHINES        | Machines will be tested.        | "raspberrypi3 qemuarm"    |
| TEST_DISTRO_FEATURES | DISTRO_FEATURES will be used.   | "pam x11"                 |

Example:
```sh
$ export TEST_TARGETS="xz-native gawk gzip"
$ export TEST_DISTROS="deby deby-tiny"
$ export TEST_MACHINES="beaglebone raspberrypi3 qemuarm"

$ ./build_test.sh              # For running test directly
$ make -C ../docker build_test # For running test in docker
```

Test result will be stored in _logs/\<distro>/\<machine>/result.txt_

## run_ptest.sh
Script for run ptest.

Input params from env:

| Variable             | Description                         | Example                   |
| -------------------- | ----------------------------------- | ------------------------- |
| TEST_TARGETS         | Recipes/packages will be run ptest. | "zlib bzip2"              |
| TEST_DISTROS         | Distros will be tested.             | "deby deby-tiny"          |
| TEST_MACHINES | Machines will be tested. Only qemu machine is supported. | "raspberrypi3 qemuarm" |
| TEST_DISTRO_FEATURES | DISTRO_FEATURES will be used.       | "pam x11"                 |

Example:
```sh
$ export TEST_TARGETS="bzip2 flex gawk gzip"
$ export TEST_MACHINES="qemux86 qemuarm"

$ ./run_ptest.sh          # For running test directly
$ make -C ../docker ptest # For running test in docker
```

Test result will be stored in _logs/\<distro>/\<machine>/result.txt_

## generate_html.sh
Generate HTML output from test result.

Just run it. No params required.