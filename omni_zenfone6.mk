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

# Sample: This is where we'd set a backup provider if we had one
# $(call inherit-product, device/sample/products/backup_overlay.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)

# Get the prebuilt list of APNs
$(call inherit-product, vendor/omni/config/gsm.mk)

# Inherit from the common Open Source product configuration
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base_telephony.mk)

# must be before including omni part
TARGET_BOOTANIMATION_SIZE := 1080p

# Inherit from our custom product configuration
$(call inherit-product, vendor/omni/config/common.mk)

# Inherit from hardware-specific part of the product configuration
$(call inherit-product, device/asus/zenfone6/device.mk)

# Discard inherited values and use our own instead.
PRODUCT_DEVICE := zenfone6
PRODUCT_NAME := omni_zenfone6
PRODUCT_BRAND := asus
PRODUCT_MODEL := ASUS_I01WD
PRODUCT_MANUFACTURER := asus

PRODUCT_BUILD_PROP_OVERRIDES += TARGET_DEVICE=WW_I01WD PRODUCT_NAME=WW_I01WD

PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_FINGERPRINT=asus/WW_I01WD/ASUS_I01WD:9/PPR2.181005.003/16.1220.1908.191-0:user/release-keys \
    PRIVATE_BUILD_DESC="WW_Phone-user 9 PPR2.181005.003 16.1220.1908.191-0 release-keys"

PLATFORM_SECURITY_PATCH_OVERRIDE := 2019-08-01