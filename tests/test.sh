#!/bin/bash

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..

TEST_TARGETS=${TEST_TARGETS:-core-image-minimal}
TEST_DISTROS=${TEST_DISTROS:-deby-tiny}
TEST_MACHINES=${TEST_MACHINES:-qemux86}

cd $WORKDIR
export TEMPLATECONF=meta-debian/conf
source ./poky/oe-init-build-env
for distro in $TEST_DISTROS; do
  sed -i -e "s/\(^DISTRO\s*?*=\).*/\1 \"$distro\"/" conf/local.conf
  for machine in $TEST_MACHINES; do
    sed -i -e "s/\(^MACHINE\s*?*=\).*/\1 \"$machine\"/" conf/local.conf

    LOGDIR=$THISDIR/logs/$TEST_DISTROS/$TEST_MACHINES
    RESULT=$LOGDIR/result.txt

    mkdir -p $LOGDIR
    rm -f $RESULT
    touch $RESULT

    for target in $TEST_TARGETS; do
      bitbake $target 2>&1 | tee $LOGDIR/${target}.log

      if [ "$?" = "0" ]; then
        echo "$target pass" >> $RESULT
      else
        echo "$target fail" >> $RESULT
      fi
    done
  done
done
