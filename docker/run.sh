#!/bin/sh -ex
# Copyright (c) 2025 Roger Brown.
# Licensed under the MIT License.

docker run --rm -d --name my-radius -p 1812-1813:1812-1813/udp my-radius-image
