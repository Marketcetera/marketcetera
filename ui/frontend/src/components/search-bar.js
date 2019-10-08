import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-checkbox/src/vaadin-checkbox.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '../../styles/shared-styles.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
import { Debouncer } from '@polymer/polymer/lib/utils/debounce.js';
import { timeOut } from '@polymer/polymer/lib/utils/async.js';
class SearchBar extends PolymerElement {
  static get template() {
    return html`
    <style include="shared-styles">
      :host {
        position: relative;
        z-index: 2;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        padding: 0 var(--lumo-space-s);
        background-image: linear-gradient(var(--lumo-shade-20pct), var(--lumo-shade-20pct));
        background-color: var(--lumo-base-color);
        box-shadow: 0 0 16px 2px var(--lumo-shade-20pct);
        order: 1;
        width: 100%;
        height: 48px;
      }

      .row {
        display: flex;
        align-items: center;
        height: 3em;
      }

      .checkbox,
      .clear-btn,
      :host([show-extra-filters]) .action-btn {
        display: none;
      }

      :host([show-extra-filters]) .clear-btn {
        display: block;
      }

      :host([show-checkbox]) .checkbox.mobile {
        display: block;
        transition: all 0.5s;
        height: 0;
      }

      :host([show-checkbox][show-extra-filters]) .checkbox.mobile {
        height: 2em;
      }

      .field {
        flex: 1;
        width: auto;
        padding-right: var(--lumo-space-s);
      }

      @media (min-width: 700px) {
        :host {
          order: 0;
        }

        .row {
          width: 100%;
          max-width: 964px;
          margin: 0 auto;
        }

        .field {
          padding-right: var(--lumo-space-m);
        }

        :host([show-checkbox][show-extra-filters]) .checkbox.desktop {
          display: block;
        }

        :host([show-checkbox][show-extra-filters]) .checkbox.mobile {
          display: none;
        }
      }
    </style>

    <div class="row">
      <vaadin-text-field id="field" class="field" placeholder="[[fieldPlaceholder]]" value="{{fieldValue}}" on-focus="_onFieldFocus" 
        on-blur="_onFieldBlur" theme="white">
        <iron-icon icon\$="[[fieldIcon]]" slot="prefix"></iron-icon>
      </vaadin-text-field>
      <vaadin-checkbox class="checkbox desktop" checked="{{checkboxChecked}}" on-focus="_onFieldFocus" 
        on-blur="_onFieldBlur">[[checkboxText]]</vaadin-checkbox>
      <vaadin-button id="clear" class="clear-btn" theme="tertiary">
        [[clearText]]
      </vaadin-button>
      <vaadin-button id="action" class="action-btn" theme="primary">
        <iron-icon icon\$="[[buttonIcon]]" slot="prefix"></iron-icon>
        [[buttonText]]
      </vaadin-button>
    </div>

    <vaadin-checkbox class="checkbox mobile" checked="{{checkboxChecked}}" on-focus="_onFieldFocus" 
      on-blur="_onFieldBlur">[[checkboxText]]</vaadin-checkbox>
`;
  }

  static get is() {
    return 'search-bar';
  }
  static get properties() {
    return {
      fieldPlaceholder: {
        type: String
      },
      fieldValue: {
        type: String,
        notify: true
      },
      fieldIcon: {
        type: String,
        value: 'vaadin:search'
      },
      buttonIcon: {
        type: String,
        value: 'vaadin:plus'
      },
      buttonText: {
        type: String
      },
      showCheckbox: {
        type: Boolean,
        value: false,
        reflectToAttribute: true
      },
      checkboxText: {
        type: String
      },
      checkboxChecked: {
        type: Boolean,
        notify: true
      },
      clearText: {
        type: String,
        value: 'Clear search'
      },
      showExtraFilters: {
        type: Boolean,
        value: false,
        reflectToAttribute: true
      },
      _isSafari: {
        type: Boolean,
        value: function() {
          return /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
        }
      }
    };
  }

  static get observers() {
    return [
      '_setShowExtraFilters(fieldValue, checkboxChecked, _focused)'
    ];
  }

  ready() {
    super.ready();
    // In iOS prevent body scrolling to avoid going out of the viewport
    // when keyboard is opened
    this.addEventListener('touchmove', e => e.preventDefault());
  }

  _setShowExtraFilters(fieldValue, checkboxChecked, focused) {
    this._debouncer = Debouncer.debounce(
      this._debouncer,
      // Set 1 millisecond wait to be able move from text field to checkbox with tab.
      timeOut.after(1), () => {
        this.showExtraFilters = fieldValue || checkboxChecked || focused;

        // Safari has an issue with repainting shadow root element styles when a host attribute changes.
        // Need this workaround (toggle any inline css property on and off) until the issue gets fixed.
        // Issue is fixed in Safari 11 Tech Preview version.
        if (this._isSafari && this.root) {
          Array.from(this.root.querySelectorAll('*')).forEach(function(el) {
            el.style['-webkit-backface-visibility'] = 'visible';
            el.style['-webkit-backface-visibility'] = '';
          });
        }
      }
    );
  }

  _onFieldFocus(e) {
    e.target == this.$.field && this.dispatchEvent(new Event('search-focus', {bubbles: true, composed: true}));
    this._focused = true;
  }

  _onFieldBlur(e) {
    e.target == this.$.field && this.dispatchEvent(new Event('search-blur', {bubbles: true, composed: true}));
    this._focused = false;
  }
}

window.customElements.define(SearchBar.is, SearchBar);
