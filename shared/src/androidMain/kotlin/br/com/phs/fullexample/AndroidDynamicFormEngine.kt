package br.com.phs.fullexample

class AndroidDynamicFormEngine : DynamicFormEngine(
    source = BundledSampleLoginFormSource,
    actionHandler = SampleLoginActionHandler,
)
