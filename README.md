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

Yes, the ENDEACH tag is required in order to end a for-each block. Bad stuff will happen otherwise. See **bin/example.template** for additional examples.

## License

The MIT License (MIT)

Copyright (c) 2014 Jon Stout

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
