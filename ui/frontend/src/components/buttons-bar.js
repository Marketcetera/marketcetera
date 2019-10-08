import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
{
  class ButtonsBarElement extends PolymerElement {
    static get template() {
      return html`
    <style>
      :host {
        flex: none;
        display: flex;
        flex-wrap: wrap;
        transition: box-shadow .2s;
        justify-content: space-between;
        padding-top: var(--lumo-space-s);
        align-items: baseline;
        box-shadow: 0 -3px 3px -3px var(--lumo-shade-20pct);
      }

      :host([no-scroll]) {
        box-shadow: none;
      }

      :host ::slotted([slot=info]),
      .info {
        text-align: right;
        flex: 1;
      }

      ::slotted(vaadin-button) {
        margin: var(--lumo-space-xs);
      }

      @media (max-width: 600px) {
        :host ::slotted([slot=info]) {
          order: -1;
          min-width: 100%;
          flex-basis: 100%;
        }
      }
    </style>

    <slot name="left"></slot>
    <slot name="info"><div class="info"></div></slot>
    <slot name="right"></slot>
`;
    }

    static get is() {
      return 'buttons-bar';
    }
  }

  window.customElements.define(ButtonsBarElement.is, ButtonsBarElement);
}
