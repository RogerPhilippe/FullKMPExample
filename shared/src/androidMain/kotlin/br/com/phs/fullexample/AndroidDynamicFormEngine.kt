package br.com.phs.fullexample

import br.com.phs.fullexample.dynamicform.sample.BundledSampleLoginFormSource
import br.com.phs.fullexample.dynamicform.sample.SampleLoginActionHandler

class AndroidDynamicFormEngine : DynamicFormEngine(
    source = BundledSampleLoginFormSource,
    actionHandler = SampleLoginActionHandler,
)
