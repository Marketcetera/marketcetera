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
 Copyright (C) 2003, 2004, 2005, 2007 StatPro Italia srl

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
 * Pricing engine for vanilla options using Monte Carlo simulation
 *
 * @category vanillaengines
 *
 * @author Richard Gomes
 */
public class MCVanillaEngine /*extends McSimulation<MC extends Variate, RNG extends RandomNumberGenerator, S>*/ {

    public MCVanillaEngine() {
        throw new UnsupportedOperationException("work in progress");
    }

}


//template <template <class> class MC, class RNG,
//class S = Statistics, class Inst = VanillaOption>
//class MCVanillaEngine : public Inst::engine,
//              public McSimulation<MC,RNG,S> {
//public:
//void calculate() const {
//McSimulation<MC,RNG,S>::calculate(requiredTolerance_,
//                                requiredSamples_,
//                                maxSamples_);
//this->results_.value = this->mcModel_->sampleAccumulator().mean();
//if (RNG::allowsErrorEstimate)
//this->results_.errorEstimate =
//  this->mcModel_->sampleAccumulator().errorEstimate();
//}
//protected:
//typedef typename McSimulation<MC,RNG,S>::path_generator_type
//path_generator_type;
//typedef typename McSimulation<MC,RNG,S>::path_pricer_type
//path_pricer_type;
//typedef typename McSimulation<MC,RNG,S>::stats_type
//stats_type;
//typedef typename McSimulation<MC,RNG,S>::result_type
//result_type;
//// constructor
//MCVanillaEngine(Size timeSteps,
//          Size timeStepsPerYear,
//          bool brownianBridge,
//          bool antitheticVariate,
//          bool controlVariate,
//          Size requiredSamples,
//          Real requiredTolerance,
//          Size maxSamples,
//          BigNatural seed);
//// McSimulation implementation
//TimeGrid timeGrid() const;
//boost::shared_ptr<path_generator_type> pathGenerator() const {
//
//Size dimensions = this->arguments_.stochasticProcess->factors();
//TimeGrid grid = this->timeGrid();
//typename RNG::rsg_type generator =
//  RNG::make_sequence_generator(dimensions*(grid.size()-1),seed_);
//return boost::shared_ptr<path_generator_type>(
//     new path_generator_type(this->arguments_.stochasticProcess,
//                             grid, generator, brownianBridge_));
//}
//result_type controlVariateValue() const;
//// data members
//Size timeSteps_, timeStepsPerYear_;
//Size requiredSamples_, maxSamples_;
//Real requiredTolerance_;
//bool brownianBridge_;
//BigNatural seed_;
//};
//
//
//// template definitions
//
//template <template <class> class MC, class RNG, class S, class Inst>
//inline MCVanillaEngine<MC,RNG,S,Inst>::MCVanillaEngine(Size timeSteps,
//                                        Size timeStepsPerYear,
//                                        bool brownianBridge,
//                                        bool antitheticVariate,
//                                        bool controlVariate,
//                                        Size requiredSamples,
//                                        Real requiredTolerance,
//                                        Size maxSamples,
//                                        BigNatural seed)
//: McSimulation<MC,RNG,S>(antitheticVariate, controlVariate),
//timeSteps_(timeSteps), timeStepsPerYear_(timeStepsPerYear),
//requiredSamples_(requiredSamples), maxSamples_(maxSamples),
//requiredTolerance_(requiredTolerance),
//brownianBridge_(brownianBridge), seed_(seed) {}
//
//template <template <class> class MC, class RNG, class S, class Inst>
//inline typename MCVanillaEngine<MC,RNG,S,Inst>::result_type
//MCVanillaEngine<MC,RNG,S,Inst>::controlVariateValue() const {
//
//boost::shared_ptr<PricingEngine> controlPE =
//this->controlPricingEngine();
//QL_REQUIRE(controlPE,
//     "engine does not provide "
//     "control variation pricing engine");
//
//typename Inst::arguments* controlArguments =
//  dynamic_cast<typename Inst::arguments*>(
//                                     controlPE->getArguments());
//
//QL_REQUIRE(controlArguments, "engine is using inconsistent arguments");
//
//*controlArguments = this->arguments_;
//controlPE->calculate();
//
//const typename Inst::results* controlResults =
//  dynamic_cast<const typename Inst::results*>(
//                                       controlPE->getResults());
//QL_REQUIRE(controlResults,
//     "engine returns an inconsistent result type");
//
//return result_type(controlResults->value);
//}
//
//
//template <template <class> class MC, class RNG, class S, class Inst>
//inline TimeGrid MCVanillaEngine<MC,RNG,S,Inst>::timeGrid() const {
//Date lastExerciseDate = this->arguments_.exercise->lastDate();
//Time t = this->arguments_.stochasticProcess->time(lastExerciseDate);
//if (this->timeSteps_ != Null<Size>()) {
//return TimeGrid(t, this->timeSteps_);
//} else if (this->timeStepsPerYear_ != Null<Size>()) {
//Size steps = static_cast<Size>(this->timeStepsPerYear_*t);
//return TimeGrid(t, std::max<Size>(steps, 1));
//} else {
//QL_FAIL("time steps not specified");
//}
//}
