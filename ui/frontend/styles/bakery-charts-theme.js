import '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-charts/vaadin-chart-default-theme.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="bakery-charts-theme" theme-for="vaadin-chart">
  <template>
    <style include="vaadin-chart-default-theme">
      :host(.counter),
      :host(.counter) #chart {
        height: 120px;
      }

      :host(.counter) .highcharts-color-0 {
        fill: #55bf3b;
        stroke: #55bf3b;
      }

      :host(.counter) .highcharts-pane {
        fill: gray;
        stroke-width: 0;
      }

      :host(.counter) .highcharts-background,
      :host(.yearly-sales) .highcharts-background {
        fill: transparent;
      }

      :host(.counter) .highcharts-minor-grid-line,
      :host(.counter) .highcharts-grid-line {
        stroke: none;
      }

      :host(.column-chart) .highcharts-point.highcharts-color-0 {
        stroke: rgba(34, 70, 117, 0.5);
        fill: rgba(34, 70, 117, 0.5);
      }

      :host(.column-chart) .highcharts-xaxis .highcharts-axis-line {
        stroke: none;
        fill: none;
      }

      :host(.column-chart) .highcharts-title,
      :host(.yearly-sales) .highcharts-title,
      :host(.product-split-donut) .highcharts-title {
        fill: rgba(42, 71, 110, 0.6);
        font-size: 16px;
        font-weight: bold;
      }

      :host(.yearly-sales) .highcharts-color-0 {
        stroke: #18ddf2;
        fill: #18ddf2;
      }

      :host(.yearly-sales) .highcharts-color-1 {
        stroke: #1877f3;
        fill: #1877f3;
      }

      :host(.yearly-sales) .highcharts-color-2 {
        stroke: #1bca66;
        fill: #1bca66;
      }

      :host(.yearly-sales) .highcharts-grid-line {
        stroke: rgba(23, 68, 128, 0.1);
      }

      :host(.yearly-sales) .highcharts-area {
        fill-opacity: 0.1;
      }

      :host(.product-split-donut) .highcharts-data-label text {
        font-size: 12px;
        font-weight: bold;
        text-shadow: 0 0 6px #fff, 0 0 3px #fff;
        fill: #808080;
      }

      :host(.product-split-donut) .highcharts-axis-line {
        stroke: none;
      }

      :host(.column-chart),
      :host(.column-chart) #chart,
      :host(.yearly-sales),
      :host(.yearly-sales) #chart,
      :host(.product-split-donut),
      :host(.product-split-donut) #chart {
        height: 100%;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
