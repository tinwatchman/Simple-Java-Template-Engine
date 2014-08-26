# Simple Java Template Engine

Created for a job application. In short, a simple Java program that -- when given a formatted template and a file with JSON-formatted data -- will output HTML. 

The templating language is fairly simple. To insert a property from the JSON data, one merely needs to surround it as follows:

```
<< object.property.subproperty >>
```

Basic for-each functionality is also provided:

```
<< EACH object.arrayProperty instanceName >>
	<span> << instanceName.property >> </span>
<< ENDEACH >>
```

Yes, the ENDEACH tag is required in order to end a for-each block. Bad stuff will happen otherwise. See bin/example.template for additional examples.