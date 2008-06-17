# for the marks by symbol report
class PnlByAccount < ReportWithToFromDates
  
  attr_reader :account, :nickname

  def initialize(nickname, params, suffix)
    super(params, suffix)
    @nickname = nickname
    @account = Account.find_by_nickname(nickname)
  end
  
  def validate
    super
    errors.add(:account, nickname + " not found") unless !account.nil?
  end
end