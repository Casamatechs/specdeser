#!/usr/bin/bash

# First of all we check we at least got 1 parameter.

#if [[ "$#" -lt 1 ]]; then
#    echo "[ERROR] No benchmark was provided as argument"
#    exit 1
#fi

# We need to touch many different settings in the system that require superuser privileges,
# but will try to figure out if there's a way to avoid it.

#read -s -p "Enter Password for sudo: " sudoPW

# Disable ASLR. This will avoid random memory allocation during benchmarking
# but also enables side-channel attacks.
sudo bash -c "echo 0 > /proc/sys/kernel/randomize_va_space"

# Set all the cores into performance mode. This will set them to max frequency
# to avoid changes in cycles per second during the benchmarking.
for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor
do
    sudo bash -c "echo performance > $i"
done

# We want to use 2 cores and at the same time disable the siblings attached to those
# cores (AMD shares the ALU between couple of threads). Because core 0 can't be disabled
# we will discard pair 0-1 and go for pairs 2-3 and 4-5.

# To avoid changes of context on our benchmark, we will attach the process to a shield.
sudo cset shield -c 3,4 -k on

### THIS PART OF THE SCRIPT IS AD-HOC FOR MY MACHINE (Ryzen 5700H) ###
sudo bash -c "echo 0 > /sys/devices/system/cpu/cpu2/online"
sudo bash -c "echo 0 > /sys/devices/system/cpu/cpu5/online"

# Disable the CPU turbo to keep a stable frequency. For Intel CPU has to be done in other way.
sudo bash -c "echo 0 > /sys/devices/system/cpu/cpufreq/boost"

# Disable NMI watchdog.
sudo bash -c "echo 0 > /proc/sys/kernel/nmi_watchdog"

sudo bash -c "echo -1 > /proc/sys/kernel/perf_event_paranoid"
sudo sysctl kernel.kptr_restrict=0

# Here goes the benchmark

sudo env "GRAAL_COMPILER=$GRAAL_COMPILER" cset shield --exec -- perf stat ./runBenchmark

### REVERSE CHANGES ###
sudo sysctl kernel.kptr_restrict=1
sudo bash -c "echo 4 > /proc/sys/kernel/perf_event_paranoid"
sudo bash -c "echo 1 > /proc/sys/kernel/nmi_watchdog"
sudo bash -c "echo 1 > /sys/devices/system/cpu/cpufreq/boost"

sudo bash -c "echo 1 > /sys/devices/system/cpu/cpu2/online"
sudo bash -c "echo 1 > /sys/devices/system/cpu/cpu5/online"

sudo cset shield --reset

for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor
do
    sudo bash -c "echo ondemand > $i"
done

sudo bash -c "echo 2 > /proc/sys/kernel/randomize_va_space"

# Change the ownership of the generated files -- this is necessary since we're executing everything as superuser

sudo chown -R $USER:$USER ./*