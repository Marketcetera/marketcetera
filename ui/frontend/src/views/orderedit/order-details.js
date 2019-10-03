import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-item.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '../../components/buttons-bar.js';
import '../../components/utils-mixin.js';
import '../storefront/order-status-badge.js';
import '../../../styles/shared-styles.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
class OrderDetails extends window.ScrollShadowMixin(PolymerElement) {
  static get template() {
    return html`
    <style include="shared-styles">
      :host {
        display: flex;
        flex-direction: column;
        box-sizing: border-box;
        flex: auto;
      }

      /*
        Workaround for non-working dom-repeat inside tables in IE11
        (https://github.com/Polymer/polymer/issues/1567):
        use divs with table-like display values instead of the actual
        <table>, <tr> and <td> elements.
      */
      .table {
        display: table;
      }

      .tr {
        display: table-row;
      }

      .td {
        display: table-cell;
      }

      .main-row {
        flex: 1;
      }

      h3 {
        margin: 0;
        word-break: break-all;
        /* Non standard for WebKit */
        word-break: break-word;
        white-space: normal;
      }

      .date,
      .time {
        white-space: nowrap;
      }

      .dim,
      .secondary {
        color: var(--lumo-secondary-text-color);
      }

      .secondary {
        font-size: var(--lumo-font-size-xs);
        line-height: var(--lumo-font-size-xl);
      }

      .meta-row {
        display: flex;
        justify-content: space-between;
        padding-bottom: var(--lumo-space-s);
      }

      .products {
        width: 100%;
      }

      .products .td {
        text-align: center;
        vertical-align: middle;
        padding: var(--lumo-space-xs);
        border: none;
        border-bottom: 1px solid var(--lumo-contrast-10pct);
      }

      .products .td.product-name {
        text-align: left;
        padding-left: 0;
        width: 100%;
      }

      .products .td.number {
        text-align: right;
      }

      .products .td.money {
        text-align: right;
        padding-right: 0;
      }

      .history-line {
        margin: var(--lumo-space-xs) 0;
      }

      .comment {
        font-size: var(--lumo-font-size-s);
      }

      order-status-badge[small] {
        margin-left: 0.5em;
      }

      #sendComment {
        color: var(--lumo-primary-color-50pct);
      }

      @media (min-width: 600px) {
        .main-row {
          padding: var(--lumo-space-l);
          flex-basis: auto;
        }
      }
    </style>

    <div class="scrollable main-row" id="main">
      <div class="meta-row">
        <order-status-badge status="[[item.state]]"></order-status-badge>
        <span class="dim">Order #[[item.id]]</span>
      </div>

      <vaadin-form-layout id="form1">
        <vaadin-form-item>
          <label slot="label">Due</label>
          <vaadin-form-layout id="form2">
            <div class="date">
              <h3>[[item.dueDate.day]]</h3>
              <span class="dim">[[item.dueDate.weekday]]</span>
            </div>
            <div class="time">
              <h3>[[item.dueTime]]</h3>
              <span class="dim">[[item.pickupLocation.name]]</span>
            </div>
          </vaadin-form-layout>
        </vaadin-form-item>

        <vaadin-form-item colspan="2">
          <label slot="label">Customer</label>
          <h3>[[item.customer.fullName]]</h3>
        </vaadin-form-item>

        <vaadin-form-item>
          <label slot="label">Phone number</label>
          <h3>[[item.customer.phoneNumber]]</h3>
        </vaadin-form-item>
      </vaadin-form-layout>

      <vaadin-form-layout id="form3">
        <div></div>

        <vaadin-form-layout id="form4" colspan="2">
          <template is="dom-if" if="[[item.customer.details]]">
            <vaadin-form-item label-position="top">
              <label slot="label">Additional details</label>
              <span>[[item.customer.details]]</span>
            </vaadin-form-item>
          </template>

          <vaadin-form-item>
            <label slot="label">Products</label>
            <div class="table products">
              <template is="dom-repeat" items="[[item.items]]" as="item">
                <dom-if if="[[item.product.name]]">
                  <template>
                    <div class="tr">
                      <div class="td product-name">
                        <div class="bold">[[item.product.name]]</div>
                        <div class="secondary">[[item.comment]]</div>
                      </div>
                      <div class="td number">
                        <span class="count">[[item.quantity]]</span>
                      </div>
                      <div class="td dim">×</div>
                      <div class="td money">
                        [[item.product.price]]
                      </div>
                    </div>
                  </template>
                </dom-if>
              </template>
            </div>
          </vaadin-form-item>

          <vaadin-form-item id="history" label-position="top" hidden="[[review]]">
            <label slot="label">History</label>
            <template is="dom-repeat" items="[[item.history]]" as="event">
              <div class="history-line">
                <span class="bold">[[event.createdBy.firstName]]</span>
                <span class="secondary">[[event.timestamp]]</span>
                <order-status-badge status="[[event.newState]]" small=""></order-status-badge>
              </div>
              <div class="comment">[[event.message]]</div>
            </template>
          </vaadin-form-item>

          <vaadin-form-item id="comment" hidden="[[review]]">
            <vaadin-text-field id="commentField" placeholder="Add comment" class="full-width" on-keydown="_onCommentKeydown" 
              maxlength="255">
              <div slot="suffix" class="comment-suffix">
                <vaadin-button id="sendComment" theme="tertiary">Send</vaadin-button>
              </div>
            </vaadin-text-field>
          </vaadin-form-item>

        </vaadin-form-layout>
      </vaadin-form-layout>
    </div>


    <buttons-bar id="footer" no-scroll\$="[[noScroll]]">
      <vaadin-button slot="left" id="back" hidden="[[!review]]">Back</vaadin-button>
      <vaadin-button slot="left" id="cancel" hidden="[[review]]">Cancel</vaadin-button>

      <div slot="info" class="total">Total [[item.totalPrice]]</div>

      <vaadin-button slot="right" id="save" theme="primary success" hidden="[[!review]]">
        <iron-icon icon="vaadin:check" slot="suffix"></iron-icon>
        Place order</vaadin-button>
      <vaadin-button slot="right" id="edit" theme="primary" hidden="[[review]]">
        Edit order
        <iron-icon icon="vaadin:edit" slot="suffix"></iron-icon>
      </vaadin-button>
    </buttons-bar>
`;
  }

  static get is() {
    return 'order-details';
  }

  static get properties() {
    return {
      item: {
        type: Object
      }
    };
  }

  ready() {
    super.ready();

    this.$.form1.responsiveSteps = this.$.form3.responsiveSteps = [
      {columns: 1, labelsPosition: 'top'},
      {minWidth: '600px', columns: 4, labelsPosition: 'top'}
    ];

    this.$.form2.responsiveSteps = [
      {columns: 1}, {minWidth: '180px', columns: 2}
    ];

    this.$.form4.responsiveSteps = [
      {columns: 1, labelsPosition: 'top'}
    ];
  }

  _onCommentKeydown(event) {
    if (event.key === 'Enter' || event.keyCode == 13) {
      // In IE11 on button click commentField blur doesn't happen, and the value-change event is not fired
      this.$.commentField.blur();
      this.$.sendComment.click();
    }
  }
}

window.customElements.define(OrderDetails.is, OrderDetails);
