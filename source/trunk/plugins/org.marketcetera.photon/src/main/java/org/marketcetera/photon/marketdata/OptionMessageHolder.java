package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;

import quickfix.Message;

public class OptionMessageHolder implements Comparable {

	private OptionPairKey key;

	private Message callMessage;

	private Message putMessage;

	public OptionMessageHolder(OptionPairKey key, Message callMessage,
			Message putMessage) {
		this.key = key;
		this.callMessage = callMessage;
		this.putMessage = putMessage;
	}

	public Message getCallMessage() {
		return callMessage;
	}

	public Message getPutMessage() {
		return putMessage;
	}

	public OptionPairKey getKey() {
		return key;
	}

	public int compareTo(Object o) {
		if (!(o instanceof OptionMessageHolder))
			return 0;
		OptionPairKey otherKey = ((OptionMessageHolder) o).getKey();
		if (otherKey == null)
			return 0;
		return this.key.compareTo(otherKey);

	}

	/**
	 * key for option message holders.
	 */
	public static class OptionPairKey implements Comparable {
		private String expirationYear;

		private String expirationMonth;

		private BigDecimal strikePrice;

		private String optionRoot;

		public OptionPairKey(String underlyingSymbol, String optionRoot,
				String expirationYear, String expirationMonth,
				BigDecimal strikePrice) {
			this.optionRoot = optionRoot;
			this.expirationYear = expirationYear;
			this.expirationMonth = expirationMonth;
			this.strikePrice = strikePrice;
		}

		public String getExpirationMonth() {
			return expirationMonth;
		}

		public String getExpirationYear() {
			return expirationYear;
		}

		public BigDecimal getStrikePrice() {
			return strikePrice;
		}

		public String getOptionRoot() {
			return optionRoot;
		}

		public int compareTo(Object o) {
			if (!(o instanceof OptionPairKey))
				return 0;
			OptionPairKey other = (OptionPairKey) o;

			int compareStrike = this.getStrikePrice().compareTo(
					other.getStrikePrice());
			if (compareStrike != 0)
				return compareStrike;

			String thisOptionRoot = this.getOptionRoot();
			String otherOptionRoot = other.getOptionRoot();
			int compareOptionRoot = thisOptionRoot
					.compareToIgnoreCase(otherOptionRoot);
			if (compareOptionRoot != 0)
				return compareOptionRoot;

			int compareExpYear = this.getExpirationYear().compareTo(
					other.getExpirationYear());
			if (compareExpYear != 0)
				return compareExpYear;

			int compareExpMonth = this.getExpirationMonth().compareTo(
					other.getExpirationMonth());
			return compareExpMonth;

		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME
					* result
					+ ((expirationMonth == null) ? 0 : expirationMonth
							.hashCode());
			result = PRIME
					* result
					+ ((expirationYear == null) ? 0 : expirationYear.hashCode());
			result = PRIME * result
					+ ((strikePrice == null) ? 0 : strikePrice.hashCode());
			result = PRIME * result
					+ ((optionRoot == null) ? 0 : optionRoot.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final OptionPairKey other = (OptionPairKey) obj;
			if (strikePrice == null) {
				if (other.strikePrice != null)
					return false;
			} else if (!strikePrice.equals(other.strikePrice))
				return false;
			if (optionRoot == null) {
				if (other.optionRoot != null)
					return false;
			} else if (!optionRoot.equals(other.optionRoot))
				return false;
			if (expirationMonth == null) {
				if (other.expirationMonth != null)
					return false;
			} else if (!expirationMonth.equals(other.expirationMonth))
				return false;
			if (expirationYear == null) {
				if (other.expirationYear != null)
					return false;
			} else if (!expirationYear.equals(other.expirationYear))
				return false;
			return true;
		}

	}
}
