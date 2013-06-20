/*
 Copyright (C) 2007 Richard Gomes

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

/*
 Copyright (C) 2003 Ferdinando Ametrano
 Copyright (C) 2000, 2001, 2002, 2003 RiskMap srl

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
*/

package org.jquantlib.pricingengines.vanilla;

/**
 * European option pricing engine using Monte Carlo simulation
 *
 * @category vanillaengines
 *
 * @author Richard Gomes
 */
//test the correctness of the returned value is tested by checking it against analytic results.
public class MCEuropeanEngine /*extends MCVanillaEngine<
        SingleVariate extends Variate,
        RNG extends RandomNumberGenerator,
        S extends Statistics>*/ {

    public MCEuropeanEngine() {
        throw new UnsupportedOperationException("work in progress");
    }

}


//template <class RNG = PseudoRandom, class S = Statistics>
//class MCEuropeanEngine : public MCVanillaEngine<SingleVariate,RNG,S> {
//  public:
//    typedef
//    typename MCVanillaEngine<SingleVariate,RNG,S>::path_generator_type
//        path_generator_type;
//    typedef
//    typename MCVanillaEngine<SingleVariate,RNG,S>::path_pricer_type
//        path_pricer_type;
//    typedef typename MCVanillaEngine<SingleVariate,RNG,S>::stats_type
//        stats_type;
//    // constructor
//    MCEuropeanEngine(Size timeSteps,
//                     Size timeStepsPerYear,
//                     bool brownianBridge,
//                     bool antitheticVariate,
//                     bool controlVariate,
//                     Size requiredSamples,
//                     Real requiredTolerance,
//                     Size maxSamples,
//                     BigNatural seed);
//  protected:
//    boost::shared_ptr<path_pricer_type> pathPricer() const;
//};
//
////! Monte Carlo European engine factory
//template <class RNG = PseudoRandom, class S = Statistics>
//class MakeMCEuropeanEngine {
//  public:
//    MakeMCEuropeanEngine();
//    // named parameters
//    MakeMCEuropeanEngine& withSteps(Size steps);
//    MakeMCEuropeanEngine& withStepsPerYear(Size steps);
//    MakeMCEuropeanEngine& withBrownianBridge(bool b = true);
//    MakeMCEuropeanEngine& withSamples(Size samples);
//    MakeMCEuropeanEngine& withTolerance(Real tolerance);
//    MakeMCEuropeanEngine& withMaxSamples(Size samples);
//    MakeMCEuropeanEngine& withSeed(BigNatural seed);
//    MakeMCEuropeanEngine& withAntitheticVariate(bool b = true);
//    MakeMCEuropeanEngine& withControlVariate(bool b = true);
//    // conversion to pricing engine
//    operator boost::shared_ptr<PricingEngine>() const;
//  private:
//    bool antithetic_, controlVariate_;
//    Size steps_, stepsPerYear_, samples_, maxSamples_;
//    Real tolerance_;
//    bool brownianBridge_;
//    BigNatural seed_;
//};
//
//class EuropeanPathPricer : public PathPricer<Path> {
//  public:
//    EuropeanPathPricer(Option::Type type,
//                       Real strike,
//                       DiscountFactor discount);
//    Real operator()(const Path& path) const;
//  private:
//    PlainVanillaPayoff payoff_;
//    DiscountFactor discount_;
//};
//
//
//// inline definitions
//
//template <class RNG, class S>
//inline
//MCEuropeanEngine<RNG,S>::MCEuropeanEngine(Size timeSteps,
//                                          Size timeStepsPerYear,
//                                          bool brownianBridge,
//                                          bool antitheticVariate,
//                                          bool controlVariate,
//                                          Size requiredSamples,
//                                          Real requiredTolerance,
//                                          Size maxSamples,
//                                          BigNatural seed)
//: MCVanillaEngine<SingleVariate,RNG,S>(timeSteps,
//                                       timeStepsPerYear,
//                                       brownianBridge,
//                                       antitheticVariate,
//                                       controlVariate,
//                                       requiredSamples,
//                                       requiredTolerance,
//                                       maxSamples,
//                                       seed) {}
//
//
//template <class RNG, class S>
//inline
//boost::shared_ptr<QL_TYPENAME MCEuropeanEngine<RNG,S>::path_pricer_type>
//MCEuropeanEngine<RNG,S>::pathPricer() const {
//
//    boost::shared_ptr<PlainVanillaPayoff> payoff =
//        boost::dynamic_pointer_cast<PlainVanillaPayoff>(
//            this->arguments_.payoff);
//    QL_REQUIRE(payoff, "non-plain payoff given");
//
//    boost::shared_ptr<GeneralizedBlackScholesProcess> process =
//        boost::dynamic_pointer_cast<GeneralizedBlackScholesProcess>(
//            this->arguments_.stochasticProcess);
//    QL_REQUIRE(process, "Black-Scholes process required");
//
//    return boost::shared_ptr<
//                   QL_TYPENAME MCEuropeanEngine<RNG,S>::path_pricer_type>(
//      new EuropeanPathPricer(
//          payoff->optionType(),
//          payoff->strike(),
//          process->riskFreeRate()->discount(this->timeGrid().back())));
//}
//
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>::MakeMCEuropeanEngine()
//: antithetic_(false), controlVariate_(false),
//  steps_(Null<Size>()), stepsPerYear_(Null<Size>()),
//  samples_(Null<Size>()), maxSamples_(Null<Size>()),
//  tolerance_(Null<Real>()), brownianBridge_(false), seed_(0) {}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withSteps(Size steps) {
//    steps_ = steps;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withStepsPerYear(Size steps) {
//    stepsPerYear_ = steps;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withSamples(Size samples) {
//    QL_REQUIRE(tolerance_ == Null<Real>(),
//               "tolerance already set");
//    samples_ = samples;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withTolerance(Real tolerance) {
//    QL_REQUIRE(samples_ == Null<Size>(),
//               "number of samples already set");
//    QL_REQUIRE(RNG::allowsErrorEstimate,
//               "chosen random generator policy "
//               "does not allow an error estimate");
//    tolerance_ = tolerance;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withMaxSamples(Size samples) {
//    maxSamples_ = samples;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withSeed(BigNatural seed) {
//    seed_ = seed;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withBrownianBridge(bool brownianBridge) {
//    brownianBridge_ = brownianBridge;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withAntitheticVariate(bool b) {
//    antithetic_ = b;
//    return *this;
//}
//
//template <class RNG, class S>
//inline MakeMCEuropeanEngine<RNG,S>&
//MakeMCEuropeanEngine<RNG,S>::withControlVariate(bool b) {
//    controlVariate_ = b;
//    return *this;
//}
//
//template <class RNG, class S>
//inline
//MakeMCEuropeanEngine<RNG,S>::operator boost::shared_ptr<PricingEngine>()
//                                                                  const {
//    QL_REQUIRE(steps_ != Null<Size>() || stepsPerYear_ != Null<Size>(),
//               "number of steps not given");
//    QL_REQUIRE(steps_ == Null<Size>() || stepsPerYear_ == Null<Size>(),
//               "number of steps overspecified");
//    return boost::shared_ptr<PricingEngine>(new
//        MCEuropeanEngine<RNG,S>(steps_,
//                                stepsPerYear_,
//                                brownianBridge_,
//                                antithetic_,
//                                controlVariate_,
//                                samples_, tolerance_,
//                                maxSamples_,
//                                seed_));
//}
//
//
//
//inline EuropeanPathPricer::EuropeanPathPricer(Option::Type type,
//                                              Real strike,
//                                              DiscountFactor discount)
//: payoff_(type, strike), discount_(discount) {
//    QL_REQUIRE(strike>=0.0,
//               "strike less than zero not allowed");
//}
//
//inline Real EuropeanPathPricer::operator()(const Path& path) const {
//    QL_REQUIRE(path.length() > 0, "the path cannot be empty");
//    return payoff_(path.back()) * discount_;
//}
