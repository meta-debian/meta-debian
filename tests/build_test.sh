#!/bin/bash

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..

TEST_TARGETS=${TEST_TARGETS:-core-image-minimal}
TEST_DISTROS=${TEST_DISTROS:-deby-tiny}
TEST_MACHINES=${TEST_MACHINES:-qemux86}

# Setup builddir
cd $WORKDIR
export TEMPLATECONF=meta-debian/conf
source ./poky/oe-init-build-env

# Get version of recipes
all_versions=`pwd`/all_versions.txt
bitbake -s > $all_versions

for distro in $TEST_DISTROS; do
  sed -i -e "s/\(^DISTRO\s*?*=\).*/\1 \"$distro\"/" conf/local.conf
  for machine in $TEST_MACHINES; do
    sed -i -e "s/\(^MACHINE\s*?*=\).*/\1 \"$machine\"/" conf/local.conf

    LOGDIR=$THISDIR/logs/$distro/$machine
    RESULT=$LOGDIR/result.txt

    mkdir -p $LOGDIR
    rm -f $RESULT
    touch $RESULT

    for target in $TEST_TARGETS; do
      version=`grep "^$target\s*:" $all_versions | cut -d: -f2 | sed "s/ *$//"`
      bitbake $target 2>&1 > $LOGDIR/${target}.log

      if [ "$?" = "0" ]; then
        echo "$target $version PASS" >> $RESULT
      else
        echo "$target $version FAIL" >> $RESULT
      fi
    done
  done
done
