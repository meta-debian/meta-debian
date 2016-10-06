FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# name of the SPL (Second Program Loader) binary
SPL_BINARY = "MLO"

# additional environment variable settings
UBOOT_ENV ?= "uEnv"
SRC_URI_append = " file://${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"
