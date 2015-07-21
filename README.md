# Dynatrace-Docker-Monitor-Plugin
Retrieves CPU, memory and network stats from Docker containers via the Docker Remote API. It retrieves the following metrics using the Remote API

1. CPU
  * total_usage
  * usage_in_usermode
  * system_cpu_usage
  * total_usage_delta
  * usage_in_usermode_delta
  * system_cpu_usage_delta

2. Memory
  * usage
  * limit
  * memoryUsage

3. Network
  * rx_bytes
  * tx_bytes
  * rx_packets
  * tx_packets
  * rx_bytes_delta
  * tx_bytes_delta
  * rx_packets_delta
  * tx_packets_delta

For all the delta calculations, the plugin does a diff between the current value and the previous value. For memoryUsage, it calculates the value by using (usage *100) / limit.

The monitor can run in three different modes:
1. TCP Port
2. Unix Socket - SSH
3. Unix Socket - Local

* TCP Port: If the docker daemon binds to a TCP Port, use this option and provide the port number the docker daemon is listening on.
* Unix Socket - SSH:  By default, docker daemon binds to a Unix socket (/var/run/docker.sock). If the docker daemon is using the default binding, you can collect metrics data from a collector that is running on a different host than the docker deaemon by using the Unix Socket - SSH option. For this option to work, ensure that
  1. Netcat utility "nc" is installed on the docker host machine.
  2. The userId provided in the monitor has sudo access. 

* Unix Socket - Local: By default, docker daemon binds to a Unix socket (/var/run/docker.sock). If the docker daemon is using the default binding, you can collect metrics data from a collector that is running on the same host as the docker deaemon by using the Unix Socket - Local option. There is no need to install Netcat "nc" utility on the host machine.

This monitor plugin works for docker daemon running on any flavor of 64-bit Linux host. 

![DockerDashboard](https://cloud.githubusercontent.com/assets/11229039/8806572/494cc32c-2fa5-11e5-8af4-05a86ffcf266.png "Docker Dashboard")
