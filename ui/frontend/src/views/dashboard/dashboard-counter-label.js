import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
class DashboardCounterLabel extends PolymerElement {
  static get template() {
    return html`
    <style>
      :host {
        position: relative;
        text-align: center;
        height: calc(18vh - 64px);
        min-height: 180px;
        display: block;
      }

      :host(.green) .count-digit {
        color: #55bf3b;
      }

      :host(.red) .count-digit {
        color: #ff473a;
      }

      :host(.blue) .count-digit {
        color: #1877f3;
      }

      :host(.gray) .count-digit {
        color: rgba(45, 71, 105, 0.7);
      }

      .content {
        padding: 10px;
      }

      .count-wrapper {
        display: block;
        text-align: center;
        padding-top: 12px;
        margin-bottom: 18px;
      }

      .count-digit {
        font-size: 44px;
      }

      .subtitle {
        color: var(--lumo-secondary-text-color);
        font-size: 14px;
      }

      h4 {
        margin: 0;
      }

      .chart-wrapper {
        position: absolute;
        top: 0;
        left: 0;
        height: 120px;
        width: 100%;
      }
    </style>

    <div class="chart-wrapper">
      <slot></slot>
    </div>

    <div class="content">
      <div class="count-wrapper">
        <span id="count" class="count-digit">[[num]]</span>
      </div>

      <h4 id="title">[[title]]</h4>
      <div id="subtitle" class="subtitle">[[subtitle]]</div>
    </div>
`;
  }

  static get is() {
    return 'dashboard-counter-label';
  }
}

window.customElements.define(DashboardCounterLabel.is, DashboardCounterLabel);
