# Copyright (C) 2010 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# This file is the build configuration for a full Android
# build for grouper hardware. This cleanly combines a set of
# device-specific aspects (drivers) with a device-agnostic
# product configuration (apps).
#

# Inherit from asus sm8250-common
-include device/asus/sm8150-common/BoardConfigCommon.mk

DEVICE_PATH := device/asus/zenfone6

# Bluetooth
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(DEVICE_PATH)/bluetooth

# HIDL
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE += $(DEVICE_PATH)/manifest/vendor_framework_compatibility_matrix.xml
DEVICE_FRAMEWORK_MANIFEST_FILE += $(DEVICE_PATH)/manifest/framework_manifest.xml
DEVICE_MANIFEST_FILE += $(DEVICE_PATH)/manifest/manifest.xml
DEVICE_MATRIX_FILE += $(DEVICE_PATH)/manifest/compatibility_matrix.xml

# Init
TARGET_INIT_VENDOR_LIB := //$(DEVICE_PATH):libinit_sm8150

# Kernel
TARGET_KERNEL_SOURCE := kernel/asus/sm8150
TARGET_KERNEL_CONFIG := vendor/zs630kl_defconfig

TARGET_MODULE_ALIASES += \
    wcd_cpe_dlkm.ko:audio_wcd_cpe.ko

# Partitions
BOARD_DTBOIMG_PARTITION_SIZE := 25165824
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3758096384
BOARD_USERDATAIMAGE_PARTITION_SIZE := 242914111488
BOARD_VENDORIMAGE_PARTITION_SIZE := 1073741824

BOARD_ROOT_EXTRA_FOLDERS := ADF APD asdf batinfo xrom

# Properties
TARGET_ODM_PROP += $(DEVICE_PATH)/odm.prop
TARGET_VENDOR_PROP += $(DEVICE_PATH)/vendor.prop

# Recovery
TARGET_RECOVERY_FSTAB := $(DEVICE_PATH)/recovery.fstab
TARGET_RECOVERY_PIXEL_FORMAT := "BGRA_8888"
TARGET_RECOVERY_UI_MARGIN_HEIGHT := 100

# Security patch level
VENDOR_SECURITY_PATCH := 2021-06-01

# Sepolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/private
BOARD_VENDOR_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/vendor

