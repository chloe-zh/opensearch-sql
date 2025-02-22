# Configuration Options

>**NOTE:** All option names are *case-insensitive*.

#### Basic Options

| Option | Description | Type | Default |
|--------|-------------|------|---------------|
| `DSN` | **D**ata **S**ource **N**ame used for configuring the connection. | string | |
| `Host` / `Server` | Hostname or IP address for the target cluster. | string | |
| `Port` | Port number on which the cluster's REST interface is listening. | string | |

#### Authentication Options

| Option | Description | Type | Default |
|--------|-------------|------|---------------|
| `Auth` | Authentication mechanism to use. | one of `BASIC` (basic HTTP), `AWS_SIGV4` (AWS auth), `NONE` | `NONE`
| `User` / `UID` | [`Auth=BASIC`] Username for the connection. | string | |
| `Password` / `PWD` | [`Auth=BASIC`] Password for the connection. | string | |
| `Region` | [`Auth=AWS_SIGV4`] Region used for signing requests | AWS region (eg. `us-west-1`) | |

#### Advanced Options

| Option | Description | Type | Default |
|--------|-------------|------|---------------|
| `UseSSL` | Whether to establish the connection over SSL/TLS | boolean (`0` or `1`) | false (`0`) |
| `HostnameVerification` | Indicate whether certificate hostname verification should be performed for an SSL/TLS connection. | boolean (`0` or `1`) | true (`1`) |
| `ResponseTimeout` | The maximum time to wait for responses from the `Host`, in seconds. | integer | `10` |
| `FetchSize` | The page size for all cursor requests. The default value (-1) uses server-defined page size. Set FetchSize to 0 for non-cursor behavior. | integer | `-1` |

#### Logging Options

| Option | Description | Type | Default |
|--------|-------------|------|---------------|
| `LogLevel` | Severity level for driver logs. | one of `OPENSEARCH_OFF`, `OPENSEARCH_FATAL`, `OPENSEARCH_ERROR`, `OPENSEARCH_INFO`, `OPENSEARCH_DEBUG`, `OPENSEARCH_TRACE`, `OPENSEARCH_ALL` | `OPENSEARCH_WARNING` |
| `LogOutput` | Location for storing driver logs. | string | WIN: `C:\`, MAC: `/tmp` |

**NOTE:** Administrative privileges are required to change the value of logging options on Windows.