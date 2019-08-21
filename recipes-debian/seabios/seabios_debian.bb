SUMMARY = "Legacy BIOS implementation"
HOMEPAGE = "http://www.seabios.org"

LICENSE = "LGPLv3 & GPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit debian-package
require recipes-debian/sources/seabios.inc

DEPENDS = "acpica-native"

DEBIAN_QUILT_PATCHES = ""

PACKAGE_ARCH = "all"

EXTRA_OEMAKE += "CC='${BUILD_CC}'"

FWS = "acpi-dsdt q35-acpi-dsdt ssdt-misc ssdt-pcihp ssdt-proc"
VGABIOS = "cirrus stdvga virtio vmware qxl isavga"
BIOS = "bios bios-256k ${VGABIOS}"

build_bios() {
    d=`basename $1`
    if [ ! -e $d/.config.old ]; then
        rm -rf $d; mkdir -p $d
        for x in $3; do
            case $x in
            (*=n) echo "# CONFIG_${x%=*} is not set";;
            (*) echo CONFIG_${x};;
            esac
        done > $d/.config
        oe_runmake KCONFIG_CONFIG=${S}/$d/.config OUT=$d/ oldnoconfig
    fi
    oe_runmake KCONFIG_CONFIG=${S}/$d/.config OUT=$d/ $d/$2.bin
    rm -f $1; ln $d/$2.bin $1
}

# Base on debian/rules
do_compile() {
    mkdir -p build

    # upstream ships .hex files in tarball, result of iasl compilation
    # ensure it is actually built during build
    FW_FILES=""
    for i in ${FWS}; do
        FW_FILES="$FW_FILES src/fw/$i.hex"
    done
    rm -f ${FW_FILES}
    oe_runmake OUT=build/ ${FW_FILES}

    # Build bios
    for bios in ${BIOS}; do
        case $bios in
        "bios")
            # A stripped-down version of bios, to fit in 128Kb, for qemu <= 1.7
            build_bios build/bios.bin bios \
                "QEMU=y ROM_SIZE=128 ATA_DMA=y PVSCSI=n \
                 BOOTSPLASH=n XEN=n USB_OHCI=n USB_XHCI=n \
                 USB_UAS=n SDCARD=n TCGBIOS=n MPT_SCSI=n \
                 NVME=n USE_SMM=n VGAHOOKS=n";;
        "bios-256k")
            build_bios build/bios-256k.bin bios \
                "QEMU=y ROM_SIZE=256 ATA_DMA=y";;
        "cirrus")
            build_bios build/vgabios-cirrus.bin vgabios \
                "BUILD_VGABIOS=y VGA_CIRRUS=y VGA_PCI=y";;
        "stdvga")
            build_bios build/vgabios-stdvga.bin vgabios \
                "BUILD_VGABIOS=y VGA_BOCHS=y VGA_PCI=y";;
        "virtio")
            build_bios build/vgabios-virtio.bin vgabios \
                "BUILD_VGABIOS=y VGA_BOCHS=y VGA_PCI=y \
                 OVERRIDE_PCI_ID=y VGA_VID=0x1af4 VGA_DID=0x1050";;
        "vmware")
            build_bios build/vgabios-vmware.bin vgabios \
                "BUILD_VGABIOS=y VGA_BOCHS=y VGA_PCI=y \
                 OVERRIDE_PCI_ID=y VGA_VID=0x15ad VGA_DID=0x0405";;
        "qxl")
            build_bios build/vgabios-qxl.bin vgabios \
                "BUILD_VGABIOS=y VGA_BOCHS=y VGA_PCI=y \
                 OVERRIDE_PCI_ID=y VGA_VID=0x1b36 VGA_DID=0x0100";;
        "isavga")
            build_bios build/vgabios-isavga.bin vgabios \
                "BUILD_VGABIOS=y VGA_BOCHS=y VGA_PCI=n";;
        "*") ;;
        esac
    done

    oe_runmake -C debian/optionrom/
    chmod -x debian/optionrom/*.bin
        mkdir -p build && touch build/optionrom-stamp
}

do_install() {
    install -d ${D}${datadir}/seabios/optionrom
    install -m 644 build/*.bin ${D}${datadir}/seabios
    install -m 644 build/src/fw/*dsdt.aml ${D}${datadir}/seabios
    install -m 644 debian/optionrom/*.bin ${D}${datadir}/seabios

    for i in extboot.bin kvmvapic.bin linuxboot.bin multiboot.bin vapic.bin; do
        ln -sf ../$i ${D}${datadir}/seabios/optionrom/$i
    done
}

BBCLASSEXTEND = "native nativesdk"
