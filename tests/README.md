# How to use

To use this test, the build environment needs to be setup first following [Setup build environment](../README.md#setup-build-environment),
or you can use docker for automation setup.
```sh
$ make -C ../docker build_test # to run build test
$ make -C ../docker qemu_ptest # to run ptest for qemu machine
```

## build_test.sh
Script for building recipe in meta-debian.

Input params from env:

| Variable             | Description                     | Example                   |
| -------------------- | ------------------------------- | ------------------------- |
| TEST_PACKAGES        | Recipes/Packages will be built. | "zlib core-image-minimal" |
| TEST_DISTROS         | Distros will be tested.         | "deby deby-tiny"          |
| TEST_MACHINES        | Machines will be tested.        | "raspberrypi3 qemuarm"    |
| TEST_DISTRO_FEATURES | DISTRO_FEATURES will be used.   | "pam x11"                 |
| TEST_ENABLE_SECURITY_UPDATE | Enable security update repository. | "1"             |

Example:
```sh
$ export TEST_PACKAGES="xz-native gawk gzip"
$ export TEST_DISTROS="deby deby-tiny"
$ export TEST_MACHINES="beaglebone raspberrypi3 qemuarm"

$ ./build_test.sh              # For running test directly
$ make -C ../docker build_test # For running test in docker
```

Test result will be stored in _logs/\<distro>/\<machine>/result.txt_

## qemu_ptest.sh
Script for build image and run ptest inside a qemu machine.

Input params from env:

| Variable             | Description                         | Example                   |
| -------------------- | ----------------------------------- | ------------------------- |
| TEST_PACKAGES        | Recipes/packages will be run ptest. | "zlib bzip2"              |
| TEST_DISTROS         | Distros will be tested.             | "deby deby-tiny"          |
| TEST_MACHINES | Machines will be tested. Only qemu machine is supported. | "qemux86 qemuarm" |
| TEST_DISTRO_FEATURES | DISTRO_FEATURES will be used.       | "pam x11"                 |
| PTEST_RUNNER_TIMEOUT | Timeout seconds for ptest-runner.   | "7200"                    |
| QEMU_PARAMS          | Specify custom parameters to QEMU.  | "-smp 2 -m 2048"          |
| IMAGE_ROOTFS_EXTRA_SPACE | Extra space(KB) of rootfs for qemu machine. | "1048576"     |

Example:
```sh
$ export TEST_PACKAGES="bzip2 flex gawk gzip"
$ export TEST_MACHINES="qemux86 qemuarm"

$ ./qemu_ptest.sh              # For running test directly
$ make -C ../docker qemu_ptest # For running test in docker
```

Test result will be stored in _logs/\<distro>/\<machine>/result.txt_

## run_ptest.sh
Script to run ptest.
This script can run independently on target machine or be called through 'qemu_ptest.sh'.

Imput params from env:
| Variable      | Description                     | Example                   |
| --------------| ------------------------------- | ------------------------- |
| TEST_PACKAGES | Packages need to run ptest.     | "zlib gzip"               |
| PTEST_RUNNER_TIMEOUT | Timeout seconds for ptest-runner. | "7200"           |

Example:
```sh
# On the target board
$ export TEST_PACKAGES="zlib gzip"
$ ./run_ptest.sh
```

Test result will be stored in _/tmp/ptest/_.

## generate_html.sh
Generate HTML output from test result.

Just run it. No params required.
