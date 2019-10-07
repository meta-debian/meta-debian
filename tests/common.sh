#!/bin/bash
#
# Common functions for other scripts.

TEST_DISTROS=${TEST_DISTROS:-deby-tiny}
TEST_MACHINES=${TEST_MACHINES:-qemux86}
BUILDDIR=${BUILDDIR:-build}
VERBOSE=${VERBOSE:-0}

RED='\e[91m'
BLD='\e[1m'
BLD_RED='\e[1;91m'
RST='\e[0m'

function error {
	msg="$1"
	echo -e "${BLD_RED}ERROR${RST}${RED}: ${msg}${RST}"
}

function note {
	msg="$1"
	echo -e "${BLD}NOTE${RST}: ${msg}"
}

# If variable is already defined, replace it
# else define it.
# Params:
#   $1: variable which will be updated. Eg: IMAGE_INSTALL_append
#   $2: value which will be set. Eg: " bzip2 zlib"
#   $3: config file which will be modified. Eg: conf/local.conf
function add_or_replace {
	key="$1"
	val="$2"
	file="$3"

	if grep -q "^$key\s*?*=" $file 2> /dev/null; then
		sed -i -e "s#\(^$key\s*?*=\).*#\1 \"$val\"#" $file
	else
		echo "$key = \"$val\"" >> $file
	fi
}

# Set up a build directory.
# No params.
function setup_builddir {
	note "Setup build directory."
	cd $WORKDIR
	rm -rf $BUILDDIR/conf
	export TEMPLATECONF=meta-debian/conf
	source ./poky/oe-init-build-env $BUILDDIR

	if which gitproxy &> /dev/null; then
		add_or_replace "HOSTTOOLS_append" " gitproxy" conf/local.conf
	fi

	# Configurations for u-boot
	add_or_replace "UBOOT_MACHINE_qemux86" "qemu-x86_defconfig" conf/local.conf
	add_or_replace "UBOOT_MACHINE_qemux86-64" "qemu-x86_64_defconfig" conf/local.conf
	add_or_replace "UBOOT_MACHINE_qemumips" "qemu_mips_defconfig" conf/local.conf
	add_or_replace "UBOOT_MACHINE_qemuppc" "qemu-ppce500_defconfig" conf/local.conf
	add_or_replace "UBOOT_MACHINE_qemuarm" "qemu_arm_defconfig" conf/local.conf
	add_or_replace "UBOOT_MACHINE_qemuarm64" "qemu_arm64_defconfig" conf/local.conf
}

function get_all_packages {
	BTEST_PACKAGES=""
	PTEST_PACKAGES=""

	recipes=`find $THISDIR/.. -name *.bb`
	for recipe in $recipes; do
		recipe_env="bb_e.env"
		bitbake -e -b $recipe > $recipe_env

		# Get the final PN
		pn=`grep "^PN=" $recipe_env | cut -d\" -f2`
		BTEST_PACKAGES="$BTEST_PACKAGES $pn"

		# Check if ptest available
		ptest_enabled=`grep "^PTEST_ENABLED=" $recipe_env | cut -d\" -f2`
		if [ "$ptest_enabled" = "1" ]; then
			PTEST_PACKAGES="$PTEST_PACKAGES $pn"
		fi

		# Get BBCLASSEXTEND
		bbclassextend=`grep "^BBCLASSEXTEND=" $recipe_env | cut -d\" -f2`
		for variant in $bbclassextend; do
			if [ "$variant" = "native" ] || [ "$variant" = "cross" ]; then
				BTEST_PACKAGES="$BTEST_PACKAGES ${pn}-$variant"
			else
				BTEST_PACKAGES="$BTEST_PACKAGES ${variant}-$pn"
			fi
		done

		rm -f $recipe_env
	done
}
