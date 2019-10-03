import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
{
  /**
   * A pure Polymer Web Component for numeric fields.
   */
  class AmountField extends PolymerElement {
    static get template() {
      return html`
    <style>
      :host {
        display: block;
      }

      iron-icon {
        cursor: pointer;
        text-align: center;
      }

      iron-icon[disabled] {
        cursor: default;
        opacity: 0.1;
      }

      vaadin-text-field {
        --vaadin-text-field-default-width: 6em;
        flex: auto;
        display: flex;
      }
    </style>

    <vaadin-text-field id="input" part="text-field" theme="align-center" label="[[label]]" value="{{value}}" disabled="[[disabled]]" 
      pattern\$="[[pattern]]" prevent-invalid-input="" readonly="[[!editable]]">
      <iron-icon slot="prefix" icon="vaadin:minus" on-click="_minus" disabled\$="[[_minusDisabled(value, min, max, disabled, readOnly)]]">
      </iron-icon>
      <iron-icon slot="suffix" icon="vaadin:plus" on-click="_plus" disabled\$="[[_plusDisabled(value, min, max, disabled, readOnly)]]">
      </iron-icon>
    </vaadin-text-field>
`;
    }

    static get is() {
      return 'amount-field';
    }

    static get properties() {
      return {
        value: {
          type: Number,
          value: 0,
          notify: true
        },
        min: {
          type: Number,
          observer: '_minChanged',
          value: 0
        },
        max: {
          type: Number,
          observer: '_maxChanged',
          value: Number.MAX_SAFE_INTEGER || (Math.pow(2, 53) - 1) /* MSIE fallback */
        },
        disabled: {
          type: Boolean,
          value: false,
          notify: true,
          reflectToAttribute: true
        },
        editable: {
          type: Boolean
        },
        pattern: {
          type: String,
          value: '\d*'
        },
        label: String
      };
    }

    get _enabled() {
      return !this.disabled && !this.readOnly;
    }

    get _number() {
      const val = parseInt(this.value);
      return isNaN(val) ? this.min : val;
    }

    _minChanged(val) {
      this.value = this._number < val ? val : this._number;
    }

    _maxChanged(val) {
      this.value = this._number > val ? val : this._number;
    }

    _plus() {
      !this._plusDisabled() && (this.value = this._number + 1);
      this.$.input.focus();
    }

    _minus() {
      !this._minusDisabled() && (this.value = this._number - 1);
      this.$.input.focus();
    }

    _isEqual(value, other) {
      return Math.max(this.min, Math.min(this._number, this.max)) === other;
    }

    _plusDisabled(value, min, max, disabled, readOnly) {
      return !this._enabled || this._number >= this.max;
    }

    _minusDisabled(value, min, max, disabled, readOnly) {
      return !this._enabled || this._number <= this.min;
    }
  }

  window.customElements.define(AmountField.is, AmountField);
}
