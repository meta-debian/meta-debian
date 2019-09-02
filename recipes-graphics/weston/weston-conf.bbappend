do_install_qemuarm() {
        mkdir -p ${D}/${sysconfdir}/xdg/weston
        cat << EOF > ${D}/${sysconfdir}/xdg/weston/weston.ini
[core]
backend=fbdev-backend.so
EOF
}

do_install_qemuarm64() {
        mkdir -p ${D}/${sysconfdir}/xdg/weston
        cat << EOF > ${D}/${sysconfdir}/xdg/weston/weston.ini
[core]
backend=fbdev-backend.so
EOF
}

do_install_raspberrypi3-64() {
        mkdir -p ${D}/${sysconfdir}/xdg/weston
        cat << EOF > ${D}/${sysconfdir}/xdg/weston/weston.ini
[core]
backend=fbdev-backend.so
EOF
}