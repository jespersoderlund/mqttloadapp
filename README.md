# mqttloadapp
Configurable load generator for MQTT messages
== Templates ==
Two kinds of templates are supported by the load generator. JSON and CSV.

The templates cannot currently be created or edited in the GUI, but can be visualized.

The templates can contain a number of variables that will be replaced when instantiated. The form of a template parameter is a ”$” followed by the template variable. The following template parameters are supported:
* tick - The number of samples
* elapsedtime - Milliseconds since start of run
* timestamp - Unix epoch milliseconds
* <variables from metrics series> - The variable from each metrics series is also available in the template for subsitution

== LoadConfiguration ==
A load configuration is the entity that as a unit can be started and stopped. A load configuration contains:
- Template
- Rate (messages per minute)
- List of functions


=== Functions ===
Each function has a function type which will specifify the types of values and the configuration parameters supported. A function will generate a single output value based on the following input parameters:
* Tick - The number of samples that have been taken from the metricsserie
* Elapsed time - The amount of time that has passed since the metrics series configuration was started, in milliseconds

Each function specifies the output variable name, that can be used in templates.

RANDOM
Genarate a random value, either of Integer or float type.

The parameters supported are:
* type - INT or FLOAT the supported values, defaults to INT
* minvalue -The min value, inclusive, to be generated, defaults to 0
* maxvalue - The maximum value (non-inclusive) to be generated, defaults to 100

TEXT
A function which will generate a single text value, from a configured set. It can be randomly selected through a set of weights. The sum of all the weights are added and a random number is generated to select the string randomly with probability according to the weights.

The supported parameters are:
* texts - A ”/” separated list of text values to select from
* weights - A ”/” separated list of weight values. The weight value for a string is at the same postion as the ”texts” parameter

EXPR
Algorithmic expression evaluation function where the alogirhtmic language supported by the exp4j library http://www.objecthunter.net/exp4j/. For example complex algorithmic expressions "X^3+4+sin(x)*x^2” can be expressed and generate interesting behaviors over time.

In the expressions the variables ”tick” and ”elapsed” are available for modelling. Elapsed is in milliseconds

The parameters supported by the EXPR function are:
- expression -
- elapsedscalefactor - Scale factor to apply to the elapsetimestamp, e.g. with a scale factor of 1000 and 20000 ms elapsedtimestamp the variable ”elapsed” would have the value 20 when evaluated in the expressions
- tickscalefactor - Scale factor to apply to the ”tick” variable, e.g. with a scale factor of 10 and tick of 200 the variable ”tick” would have the value 20 when evaluated in the expression.

== Configuration ==
Configuration locations can be either in a local file system or in S3. All S3 locations are on the form "s3://<bucket>/<key>". When configuring the root, then all objects are accessed relative to that.

=== mqtt.properties ===
Contains properties for establishing the connection to the MQTT Gateway of Icebreaker.
The

=== config_root ===

=== template_root ===

metricsconfig_root
