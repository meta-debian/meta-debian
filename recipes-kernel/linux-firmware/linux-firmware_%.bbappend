do_install_append_raspberrypi3-64() {
    curdir=$(pwd)
    cd ${D}${nonarch_base_libdir}/firmware/brcm

    # raspberry pi3 b+
    ln -s brcmfmac43455-sdio.raspberrypi,3-model-b-plus.txt brcmfmac43455-sdio.txt
    cd $curdir
}

FILES_${PN}-bcm43455 += "${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.raspberrypi,3-model-b-plus.txt ${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt"

