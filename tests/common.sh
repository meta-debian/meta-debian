#!/bin/bash
#
# Common functions for other scripts.

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
	rm -rf build/conf
	export TEMPLATECONF=meta-debian/conf
	source ./poky/oe-init-build-env
	add_or_replace "HOSTTOOLS_append" " gitproxy" conf/local.conf
}

# Run bitbake -s to get version of all recipes.
# Params:
#   $1: target file that stores version information
function all_recipes_version {
	all_versions=$1

	note "Getting version of all recipes ..."
	bitbake -s > $all_versions
	if [ "$?" != "0" ]; then
		error "Failed to bitbake."
		exit 1
	fi
}

# Get version of recipe
# Params:
#   $1: target file that stores version information
function get_version {
	all_versions=$1

	version=`grep "^$target\s*:" $all_versions | cut -d: -f2 | sed "s/-r.*//"`
	# If version is empty or contains only space, set it to NA
	if echo $version | grep -q "^\s*$"; then
		version=NA
	fi
}
